package ng.cloudmusic.sdk

import com.google.gson.JsonObject
import ng.cloudmusic.util.gson

class NotOK(response: JsonObject) : RuntimeException(response.toString()) {
    val errorResponse = gson.fromJson(response, ErrorResponse::class.java)!!

    data class ErrorResponse(val msg: String, val code: Int)
}
