package ng.cloudmusic.sdk

import ng.cloudmusic.api.LoginApi
import ng.cloudmusic.api.util.CloudMusicApiInterceptor
import ng.cloudmusic.sdk.service.LoginService
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class CloudMusicSDK(cookieJar: CookieJar) {
    private val retrofit = Retrofit.Builder()
            .baseUrl("https://music.163.com/weapi/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create())
            .client(OkHttpClient.Builder()
                    .addInterceptor(CloudMusicApiInterceptor())
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .cookieJar(cookieJar)
                    .build())
            .build()

    val loginService by lazy { LoginService(retrofit.create(LoginApi::class.java)) }
}
