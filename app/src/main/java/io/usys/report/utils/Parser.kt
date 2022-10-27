package io.usys.report.utils

import com.google.gson.Gson
import io.realm.RealmObject
import org.json.JSONArray
import org.json.JSONObject


fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
    when (val value = this[it])
    {
        is JSONArray ->
        {
            val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
            JSONObject(map).toMap().values.toList()
        }
        is JSONObject -> value.toMap()
        JSONObject.NULL -> null
        else            -> value
    }
}

fun RealmObject.toJSON(): JSONObject? {
    tryCatch {
        val jsonString = Gson().toJson(this)
        return JSONObject(jsonString)
    }
    return null
}

fun HashMap<*,*>.toJSON(): JSONObject? {
    tryCatch {
        val jsonString = Gson().toJson(this)
        return JSONObject(jsonString)
    }
    return null
}

fun JSONObject.getDeepKey(key: String): Any? {
    if (this.has(key)) return this.get(key)
    for (tempKey in this.keys()) {
        val tempValue = this[tempKey]
        if (key == tempKey) return tempValue
        // -> If JSON Object
        if (tempValue is JSONObject) {
            val result = tempValue.getDeepKey(key)
            result?.let { return it }
        }
        //-> If JSON Array of Objects
        if (tempValue is JSONArray) {
            val condition = true
            var i = 0
            while (condition) {
                if (tempValue[i] != null) {
                    val current_obj = tempValue[i]
                    if (current_obj is JSONObject) {
                        val result = current_obj.getDeepKey(key)
                        result?.let { return it }
                    }
                    i++
                } else {
                    break
                }
            }
        }
    }
    return null
}

fun HashMap<*,*>.getSafe(obj: String, default: Any? = null) : Any? {
    if (this.containsKey(obj)) {
        return this[obj]
    }
    return default
}

fun JSONObject.getSafeString(obj: String, default:String="") : String {
    if (this.has(obj)) {
        return this.getString(obj)
    }
    return default
}

fun JSONObject.getSafeDouble(obj: String) : Double {
    if (this.has(obj)) {
        return this.getDouble(obj)
    }
    return 0.0
}

fun JSONObject.getSafeInt(obj: String) : Int {
    if (this.has(obj)) {
        return this.getInt(obj)
    }
    return 0
}

fun JSONObject.getSafeBoolean(obj: String) : Boolean {
    if (this.has(obj)) {
        return this.getBoolean(obj)
    }
    return false
}

fun JSONObject.getSafeJsonObj(obj: String) : JSONObject? {
    if (this.has(obj)) {
        return this.getJSONObject(obj)
    }
    return null
}

