package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.newUUID
import java.io.Serializable

open class PlayerEvaluationRef: RealmObject(), Serializable {
    @PrimaryKey
    var evalId: String = newUUID()
    var coachId: String? = null
    var playerId: String? = null
    var teamId: String? = null
}

open class Evaluation: RealmObject(), Serializable {
    @PrimaryKey
    var id: String = newUUID()
    var coachId: String? = null
    var playerId: String? = null
    var teamId: String? = null
    var overall_score: Int? = null
    var notes: String? = null
    var attributes: RealmList<CustomAttribute>? = null
    var technical_skills: String? = null
    var technical_skills_score: Int? = null
    var physical_fitness: String? = null
    var physical_fitness_score: Int? = null
    var tactical_understanding: String? = null
    var tactical_understanding_score: Int? = null
    var attitude: String? = null
    var attitude_score: Int? = null
    var decision_making: String? = null
    var decision_making_score: Int? = null
    var communication: String? = null
    var communication_score: Int? = null
    var teamwork: String? = null
    var teamwork_score: Int? = null
    var coachability: String? = null
    var coachability_score: Int? = null
    var versatility: String? = null
    var versatility_score: Int? = null

}