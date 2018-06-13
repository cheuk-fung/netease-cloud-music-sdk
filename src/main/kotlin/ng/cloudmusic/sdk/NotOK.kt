package ng.cloudmusic.sdk

import com.google.gson.JsonObject
import ng.cloudmusic.util.getAs

class NotOK(response: JsonObject) : RuntimeException(response.toString()) {
    val errorResponse = response.getAs<ErrorResponse>()!!

    data class ErrorResponse(val msg: String, val code: Int)
}
