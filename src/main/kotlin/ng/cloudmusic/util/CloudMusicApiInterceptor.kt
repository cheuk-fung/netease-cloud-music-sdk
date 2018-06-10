package ng.cloudmusic.util

import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import org.slf4j.LoggerFactory

class CloudMusicApiInterceptor(private val cookieJar: CookieJar) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!"POST".equals(request.method(), ignoreCase = true)) {
            return chain.proceed(request)
        }

        val newRequest = RequestRebuilder(request, cookieJar).rebuild()
        return chain.proceed(newRequest)
    }

    private class RequestRebuilder(private val originalRequest: Request, private val cookieJar: CookieJar) {
        fun rebuild(): Request = originalRequest.newBuilder()
                .url(buildUrl())
                .headers(buildHeaders())
                .post(buildEncryptedBody())
                .build()

        private fun buildUrl(): HttpUrl {
            val csrf = cookieJar.loadForRequest(originalRequest.url())
                    .find { it.name() == "__csrf" }
                    ?.value()

            val url = originalRequest.url().newBuilder()
                    .addQueryParameter("csrf_token", csrf ?: "")
                    .build()
            log.debug("buildUrl(): url={}", url)

            return url
        }

        private fun buildHeaders(): Headers {
            val headers = originalRequest.headers().newBuilder()
                    .set("Accept", "*/*")
                    .set("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4")
                    .set("Referer", "https://music.163.com/")
                    .set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
                    .build()
            log.debug("buildHeaders(): headers={}", headers)

            return headers
        }

        private fun buildEncryptedBody(): RequestBody {
            val body = getBody()
            val encryptedBody = Enigma.encryptRequestBody(body)
            log.debug("buildEncryptedBody(): encryptedBody={}", encryptedBody)

            return encryptedBody.entries
                    .fold(FormBody.Builder()) { builder, (k, v) -> builder.add(k, v) }
                    .build()
        }

        private fun getBody(): String {
            val buffer = Buffer()
            originalRequest.body()?.writeTo(buffer)
            val rawBody = buffer.readUtf8()
            val body = if (rawBody.isBlank()) "{}" else rawBody
            log.debug("getBody(): body={}", body)

            return body
        }

        companion object {
            private val log = LoggerFactory.getLogger(RequestRebuilder::class.java)
        }
    }
}
