package ng.cloudmusic.sdk

import com.google.gson.JsonObject
import io.reactivex.Observable
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import ng.cloudmusic.api.LoginApi
import ng.cloudmusic.api.RadioApi
import ng.cloudmusic.util.CloudMusicApiInterceptor
import ng.cloudmusic.sdk.service.LoginService
import ng.cloudmusic.sdk.service.RadioService
import okhttp3.CookieJar
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.POST
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.net.CookieHandler
import kotlin.reflect.KClass

class CloudMusicSDK(cookieJar: CookieJar) {
    constructor(cookieHandler: CookieHandler) : this(JavaNetCookieJar(cookieHandler))

    val loginService by lazy { LoginService(createApi(LoginApi::class)) }
    val radioService by lazy { RadioService(createApi(RadioApi::class)) }

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

    private inline fun <reified T : Any> createApi(clazz: KClass<T>): T {
        return errorProbe(retrofit.create(clazz.java))
    }

    internal inline fun <reified T> errorProbe(api: T): T {
        return Enhancer().run {
            setSuperclass(T::class.java)
            setCallback(ErrorProbeInterceptor(api))
            create() as T
        }
    }

    internal class ErrorProbeInterceptor<T>(private val api: T) : MethodInterceptor {
        override fun intercept(obj: Any, method: Method, args: Array<out Any>?, proxy: MethodProxy): Any {
            if (method.declaringClass == Object::class.java) {
                return proxy.invokeSuper(obj, args)
            }

            val result = proxy.invoke(api, args)
            if (shouldProbe(method)) {
                @Suppress("UNCHECKED_CAST")
                val response = result as Observable<JsonObject>

                return response.map {
                    if (it["code"]?.asInt != 200) {
                        throw NotOK(it)
                    }
                    it
                }
            }

            return result
        }

        private fun shouldProbe(method: Method): Boolean {
            if (!method.isAnnotationPresent(POST::class.java)) {
                return false
            }

            val returnType = method.genericReturnType as? ParameterizedType ?: return false
            return returnType.rawType == Observable::class.java
                    && returnType.actualTypeArguments contentEquals arrayOf(JsonObject::class.java)
        }
    }
}
