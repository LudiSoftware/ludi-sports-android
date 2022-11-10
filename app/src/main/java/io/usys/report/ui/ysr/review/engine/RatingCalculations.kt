package io.usys.report.ui.ysr.review.engine

import io.usys.report.utils.roundTo


fun calculateAverageRatingScore(overallScore:String?, singleScore:Float) : String {
    // Math for Rating
    val orgScore: Float = overallScore?.toFloat() ?: 0.0F
    val sumScore = orgScore + singleScore
    return (sumScore / 2).roundTo(1).toString()
}