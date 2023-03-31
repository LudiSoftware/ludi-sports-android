package io.usys.report.realm.model


import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.ui.views.cardViews.addAttribute
import java.io.Serializable


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
    // PlayMetrics attributes
    customAttributes.addAttribute("team", this.team ?: "unassigned")
    customAttributes.addAttribute("season", this.season ?: "unassigned")
    customAttributes.addAttribute("player_first_name", this.player_first_name ?: "unassigned")
    customAttributes.addAttribute("player_last_name", this.player_last_name ?: "unassigned")
    customAttributes.addAttribute("gender", this.gender ?: "unassigned")
    customAttributes.addAttribute("birth_date", this.birth_date ?: "unassigned")
    customAttributes.addAttribute("parent1_email", this.parent1_email ?: "unassigned")
    customAttributes.addAttribute("parent1_first_name", this.parent1_first_name ?: "unassigned")
    customAttributes.addAttribute("parent1_last_name", this.parent1_last_name ?: "unassigned")
    customAttributes.addAttribute("parent1_mobile_number", this.parent1_mobile_number ?: "unassigned")
    customAttributes.addAttribute("parent2_email", this.parent2_email ?: "unassigned")
    customAttributes.addAttribute("parent2_first_name", this.parent2_first_name ?: "unassigned")
    customAttributes.addAttribute("parent2_last_name", this.parent2_last_name ?: "unassigned")
    customAttributes.addAttribute("parent2_mobile_number", this.parent2_mobile_number ?: "unassigned")
    customAttributes.addAttribute("street", this.street ?: "unassigned")
    customAttributes.addAttribute("city", this.city ?: "unassigned")
    customAttributes.addAttribute("state", this.state ?: "unassigned")
    customAttributes.addAttribute("zip", this.zip ?: "unassigned")
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
            // PlayMetrics attributes
            "team" -> playerRef.team = attribute.value
            "season" -> playerRef.season = attribute.value
            "player_first_name" -> playerRef.player_first_name = attribute.value
            "player_last_name" -> playerRef.player_last_name = attribute.value
            "gender" -> playerRef.gender = attribute.value
            "birth_date" -> playerRef.birth_date = attribute.value
            "parent1_email" -> playerRef.parent1_email = attribute.value
            "parent1_first_name" -> playerRef.parent1_first_name = attribute.value
            "parent1_last_name" -> playerRef.parent1_last_name = attribute.value
            "parent1_mobile_number" -> playerRef.parent1_mobile_number = attribute.value
            "parent2_email" -> playerRef.parent2_email = attribute.value
            "parent2_first_name" -> playerRef.parent2_first_name = attribute.value
            "parent2_last_name" -> playerRef.parent2_last_name = attribute.value
            "parent2_mobile_number" -> playerRef.parent2_mobile_number = attribute.value
            "street" -> playerRef.street = attribute.value
            "city" -> playerRef.city = attribute.value
            "state" -> playerRef.state = attribute.value
            "zip" -> playerRef.zip = attribute.value
        }
    }
    return playerRef
}
