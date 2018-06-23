package ng.cloudmusic.util

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
import ng.cloudmusic.sdk.NotOK
import retrofit2.http.POST

internal object ResponseErrorProber {
    private val byteBuddy = ByteBuddy()

    private val isPOST = isAnnotatedWith<MethodDescription>(POST::class.java)
    private val returnsJson= returnsGeneric<MethodDescription>(
            TypeDescription.Generic.Builder.parameterizedType(Single::class.java, JsonObject::class.java).build())

    internal inline fun <reified T> probe(api: T): T {
        val isDeclaredByT = isDeclaredBy<MethodDescription>(T::class.java)
        return byteBuddy
                .subclass(T::class.java)
                .method(isDeclaredByT and isPOST and returnsJson)
                .intercept(MethodDelegation.withDefaultConfiguration()
                        .withBinders(Pipe.Binder.install(ApiForwarder::class.java))
                        .to(ApiInterceptor(api)))
                .method(isDeclaredByT and not(isPOST and returnsJson))
                .intercept(MethodDelegation.to(api))
                .make()
                .load(T::class.java.classLoader)
                .loaded
                .getDeclaredConstructor()
                .newInstance()
    }

    private infix fun <S, U : S> Junction<S>.and(other: ElementMatcher<in U>): Junction<U> = this.and(other)

    internal interface ApiForwarder<in T, out R> {
        operator fun invoke(api: T): R
    }

    internal class ApiInterceptor<T>(private val api: T) {
        @Suppress("unused")
        fun intercept(@Pipe forwarder: ApiForwarder<T, Single<JsonObject>>): Single<JsonObject> {
            return forwarder(api).map { if (it["code"]?.asInt != 200) throw NotOK(it) else it }
        }
    }
}
