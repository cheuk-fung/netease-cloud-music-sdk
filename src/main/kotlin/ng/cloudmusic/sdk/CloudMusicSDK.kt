package ng.cloudmusic.sdk

import ng.cloudmusic.api.LoginApi
import ng.cloudmusic.api.RadioApi
import ng.cloudmusic.sdk.service.LoginService
import ng.cloudmusic.sdk.service.RadioService
import ng.cloudmusic.util.CloudMusicApiInterceptor
import ng.cloudmusic.util.ResponseErrorProber
import okhttp3.CookieJar
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieHandler
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

    private inline fun <reified T : Any> createApi(clazz: KClass<T>) =
            retrofit.create(clazz.java).let(ResponseErrorProber::probe)
}
