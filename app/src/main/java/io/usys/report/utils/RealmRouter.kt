package io.usys.report.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.*
import io.usys.report.model.*
import io.usys.report.ui.ysr.coach.CoachViewHolder
import io.usys.report.ui.ysr.organization.OrgViewHolder
import io.usys.report.ui.ysr.review.coach.ReviewQuestionsViewHolder
import io.usys.report.ui.ysr.review.organization.OrgReviewCommentViewHolder
import io.usys.report.ui.ysr.service.ServiceViewHolder
import io.usys.report.ui.ysr.sport.SportViewHolder

/**
 * This Class will 'route' the RecyclerView to the correct ViewHolder based on its realm 'type'.
 */
class RouterViewHolder(itemView: View, var type:String, var updateCallback:((String, String) -> Unit)?=null) : RecyclerView.ViewHolder(itemView) {

    /**     Router / RecyclerView Checklist.
     * 1. Realm Model of FireType
     * 2. Card Layout for ListItem
     * 3. ViewModel
     * 4. Add FireType to Router bind()
     * 5. Add Card Layout to getLayout() method.
     * - Create Convenience Ext Method if Wanted.
     */
    fun bind(obj: RealmObject) {
        when (type) {
            FireTypes.SPORTS -> return SportViewHolder(itemView).bind(obj as? Sport)
            FireTypes.ORGANIZATIONS -> return OrgViewHolder(itemView).bind(obj as? Organization)
            FireTypes.COACHES -> return CoachViewHolder(itemView).bind(obj as? Coach)
            FireTypes.SERVICES -> return ServiceViewHolder(itemView).bind(obj as? Service)
            FireTypes.REVIEWS -> return OrgReviewCommentViewHolder(itemView).bind(obj as? Review)
            FireTypes.REVIEW_TEMPLATES -> return ReviewQuestionsViewHolder(itemView, updateCallback).bind(obj as? Question)
        }
    }

    companion object {
        fun getLayout(type: String): Int {
            return when (type) {
                FireTypes.ORGANIZATIONS -> R.layout.card_organization_medium2
                FireTypes.SPORTS -> R.layout.card_sport_small
                FireTypes.REVIEWS -> R.layout.card_review_comment
                FireTypes.USERS -> R.layout.card_sport_small
                FireTypes.COACHES -> R.layout.card_coach_small
                FireTypes.SERVICES -> R.layout.card_service_square
                FireTypes.REVIEW_TEMPLATES -> R.layout.card_review_question_full
                else -> R.layout.card_sport_small
            }
        }
    }

}









