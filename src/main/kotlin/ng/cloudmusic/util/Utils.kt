package ng.cloudmusic.util

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken

internal val gson = Gson()

internal inline fun <reified T> JsonElement.getAs(): T = gson.fromJson<T>(this, object : TypeToken<T>() {}.type)
