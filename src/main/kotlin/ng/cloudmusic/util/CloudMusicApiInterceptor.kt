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

internal class CloudMusicApiInterceptor(private val cookieJar: CookieJar) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!"POST".equals(request.method(), ignoreCase = true)) {
            return chain.proceed(request)
        }

        return RequestRebuilder(request, cookieJar).rebuild()
                .let(chain::proceed)
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

            return originalRequest.url().newBuilder()
                    .addQueryParameter("csrf_token", csrf ?: "")
                    .build()
                    .also { log.debug("buildUrl(): url={}", it) }
        }

        private fun buildHeaders(): Headers {
            return originalRequest.headers().newBuilder()
                    .set("Accept", "*/*")
                    .set("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4")
                    .set("Referer", "https://music.163.com/")
                    .set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
                    .build()
                    .also { log.debug("buildHeaders(): headers={}", it) }
        }

        private fun buildEncryptedBody(): RequestBody {
            val encryptedBody = readBody()
                    .let(Enigma::encryptRequestBody)
                    .also { log.debug("buildEncryptedBody(): encryptedBody={}", it) }

            return encryptedBody.entries
                    .fold(FormBody.Builder()) { builder, (k, v) -> builder.add(k, v) }
                    .build()
        }

        private fun readBody(): String {
            val buffer = Buffer()
            originalRequest.body()?.writeTo(buffer)

            return buffer.readUtf8()
                    .let { if (it.isBlank()) "{}" else it }
                    .also { log.debug("readBody(): body={}", it) }
        }

        companion object {
            private val log = LoggerFactory.getLogger(RequestRebuilder::class.java)
        }
    }
}
