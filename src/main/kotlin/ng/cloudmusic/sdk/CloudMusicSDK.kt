package ng.cloudmusic.sdk

import com.google.gson.JsonObject
import io.reactivex.Single
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.implementation.bind.annotation.Pipe
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatcher.Junction
import net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith
import net.bytebuddy.matcher.ElementMatchers.isDeclaredBy
import net.bytebuddy.matcher.ElementMatchers.not
import net.bytebuddy.matcher.ElementMatchers.returnsGeneric
import ng.cloudmusic.api.LoginApi
import ng.cloudmusic.api.RadioApi
import ng.cloudmusic.sdk.service.LoginService
import ng.cloudmusic.sdk.service.RadioService
import ng.cloudmusic.util.CloudMusicApiInterceptor
import okhttp3.CookieJar
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.POST
import java.net.CookieHandler
import java.util.function.Function
import kotlin.reflect.KClass

class CloudMusicSDK(cookieJar: CookieJar) {
    constructor(cookieHandler: CookieHandler) : this(JavaNetCookieJar(cookieHandler))

    val loginService by lazy { createApi(LoginApi::class).let(::LoginService) }
    val radioService by lazy { createApi(RadioApi::class).let(::RadioService) }

    private val retrofit = Retrofit.Builder()
            .baseUrl("https://music.163.com/weapi/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder()
                    .addInterceptor(CloudMusicApiInterceptor(cookieJar))
                    .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .cookieJar(cookieJar)
                    .build())
            .build()

    private inline fun <reified T : Any> createApi(clazz: KClass<T>) = retrofit.create(clazz.java).let(this::errorProbe)

    internal inline fun <reified T> errorProbe(api: T): T {
        val isDeclaredByT = isDeclaredBy<MethodDescription>(T::class.java)
        return byteBuddy
                .subclass(T::class.java)
                .method(isDeclaredByT and isPOST and returnsJson)
                .intercept(MethodDelegation.withDefaultConfiguration()
                        .withBinders(Pipe.Binder.install(Function::class.java))
                        .to(ErrorProbeInterceptor(api)))
                .method(isDeclaredByT and not(isPOST and returnsJson))
                .intercept(MethodDelegation.to(api))
                .make()
                .load(T::class.java.classLoader)
                .loaded
                .getDeclaredConstructor()
                .newInstance()
    }

    private infix fun <S, U : S> Junction<S>.and(other: ElementMatcher<in U>) = this.and(other)

    internal class ErrorProbeInterceptor<T>(private val api: T) {
        @Suppress("unused")
        fun intercept(@Pipe pipe: Function<T, Single<JsonObject>>): Single<JsonObject> {
            return pipe.apply(api).map { if (it["code"]?.asInt != 200) throw NotOK(it) else it }
        }
    }

    internal companion object {
        val byteBuddy = ByteBuddy()

        val isPOST = isAnnotatedWith<MethodDescription>(POST::class.java)!!
        val returnsJson = returnsGeneric<MethodDescription>(TypeDescription.Generic.Builder
                .parameterizedType(Single::class.java, JsonObject::class.java).build())!!
    }
}
