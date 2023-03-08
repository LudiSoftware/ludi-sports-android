package io.usys.report.realm.model


import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.realm.getValue
import io.usys.report.realm.realm
import io.usys.report.realm.toHashMap
import io.usys.report.ui.views.addAttribute
import io.usys.report.utils.log
import java.io.Serializable
import java.lang.reflect.Modifier


open class CustomAttribute : RealmObject(), Serializable {
    @PrimaryKey
    var key: String? = ""
    var value: String? = null

    fun add(key: String, value: String) {
        this.key = key
        this.value = value
    }
}
fun PlayerRef.toCustomAttributesList(): RealmList<CustomAttribute> {
    val customAttributes = RealmList<CustomAttribute>()
    customAttributes.addAttribute("id", this.id ?: "0")
    customAttributes.addAttribute("playerId", this.playerId ?: "0")
    customAttributes.addAttribute("name", this.name ?: "0")
    customAttributes.addAttribute("rank", this.rank?.toString() ?: "0")
    customAttributes.addAttribute("number", this.number.toString())
    customAttributes.addAttribute("tryoutTag", this.tryoutTag ?: "0")
    customAttributes.addAttribute("position", this.position ?: "0")
    customAttributes.addAttribute("foot", this.foot ?: "0")
    customAttributes.addAttribute("dob", this.dob ?: "0")
    customAttributes.addAttribute("imgUrl", this.imgUrl ?: "0")
    customAttributes.addAttribute("color", this.color ?: "0")
    return customAttributes
}

fun RealmList<CustomAttribute>.toPlayerRef(): PlayerRef {
    val playerRef = PlayerRef()
    for (attribute in this) {
        when (attribute.key) {
            "id" -> playerRef.id = attribute.value
            "playerId" -> playerRef.playerId = attribute.value
            "name" -> playerRef.name = attribute.value
            "rank" -> playerRef.rank = attribute.value?.toIntOrNull()
            "number" -> playerRef.number = attribute.value?.toIntOrNull() ?: 0
            "tryoutTag" -> playerRef.tryoutTag = attribute.value
            "position" -> playerRef.position = attribute.value
            "foot" -> playerRef.foot = attribute.value
            "dob" -> playerRef.dob = attribute.value
            "imgUrl" -> playerRef.imgUrl = attribute.value
            "color" -> playerRef.color = attribute.value
        }
    }
    return playerRef
}
