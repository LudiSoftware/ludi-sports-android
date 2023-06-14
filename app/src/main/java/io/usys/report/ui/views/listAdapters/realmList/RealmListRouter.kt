package io.usys.report.ui.views.listAdapters.realmList

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.*
import io.usys.report.realm.model.*
import io.usys.report.realm.model.users.Coach
import io.usys.report.ui.ludi.coach.CoachViewHolder
import io.usys.report.ui.ludi.note.NoteViewHolder
import io.usys.report.ui.ludi.organization.OrgViewHolder
import io.usys.report.ui.ludi.player.PlayerMediumViewHolder
import io.usys.report.ui.ludi.player.PlayerTinyViewHolder
import io.usys.report.ui.ludi.review.coach.ReviewQuestionsViewHolder
import io.usys.report.ui.ludi.review.organization.OrgReviewCommentViewHolder
import io.usys.report.ui.ludi.service.ServiceViewHolder
import io.usys.report.ui.ludi.sport.SportViewHolder
import io.usys.report.ui.views.listAdapters.rosterLiveList.PlayerTinyHorizontalViewHolder

/**
 * This Class will 'route' the RecyclerView to the correct ViewHolder based on its realm 'type'.
 */
class RouterViewHolder(itemView: View, var type:String, var updateCallback:((String, String) -> Unit)?=null, var size:String="small") : RecyclerView.ViewHolder(itemView) {

    /**     Router / RecyclerView Checklist.
     * 1. Realm Model of FireType
     * 2. Card Layout for ListItem
     * 3. ViewModel
     * 4. Add FireType to Router bind()
     * 5. Add Card Layout to getLayout() method.
     * - Create Convenience Ext Method if Wanted.
     */
    fun bind(obj: RealmObject, position: Int? = null) {
        when (type) {
            FireTypes.SPORTS -> return SportViewHolder(itemView).bind(obj as? Sport)
            FireTypes.ORGANIZATIONS -> return OrgViewHolder(itemView).bind(obj as? Organization)
            FireTypes.COACHES -> return CoachViewHolder(itemView).bind(obj as? Coach)
            FireTypes.PLAYERS -> {
                when (size) {
                    "tiny" -> return PlayerTinyViewHolder(itemView).bind(obj as? PlayerRef, position=position)
                    "tiny_horizontal" -> return PlayerTinyHorizontalViewHolder(itemView).bind(obj as? PlayerRef, position=position)
                    "medium" -> return PlayerMediumViewHolder(itemView).bind(obj as? PlayerRef, position=position)
//                    "medium_grid" -> return RosterPlayerViewHolder(itemView, ).bind(obj as? PlayerRef, position=position)
                }
            }
            FireTypes.TEAMS -> {
                when (size) {
//                    "small" -> return TeamSmallViewHolder(itemView).bind(obj as? Team)
//                    "large" -> return TeamLargeViewHolder(itemView).bind(obj as? Team)
                }
            }
            FireTypes.SERVICES -> return ServiceViewHolder(itemView).bind(obj as? Service)
            FireTypes.REVIEWS -> return OrgReviewCommentViewHolder(itemView).bind(obj as? Review)
            FireTypes.NOTES -> return NoteViewHolder(itemView).bind(obj as? Note)
            FireTypes.REVIEW_TEMPLATES -> return ReviewQuestionsViewHolder(itemView, updateCallback).bind(obj as? Question)
        }
    }

    companion object {
        fun getLayout(type: String, size:String): Int {
            return when (type) {
                FireTypes.ORGANIZATIONS -> R.layout.card_organization_medium2
                FireTypes.SPORTS -> R.layout.sport_card_list_medium
                FireTypes.REVIEWS -> R.layout.card_review_comment
                FireTypes.USERS -> R.layout.sport_card_list_medium
                FireTypes.COACHES -> R.layout.coach_card_list_medium
                FireTypes.PLAYERS -> {
                    when (size) {
                        "tiny" -> R.layout.roster_player_card_formation_deprecated
                        "medium" -> R.layout.roster_player_card_list_medium
                        "medium_grid" -> R.layout.roster_player_card_grid_medium
                        else -> R.layout.roster_player_card_formation_deprecated
                    }
                }
                FireTypes.TEAMS -> {
                    when (size) {
                        "small" -> R.layout.team_card_list_medium
                        "large" -> R.layout.team_header_card_large
                        else -> R.layout.team_card_list_medium
                    }
                }
                FireTypes.NOTES -> R.layout.note_view_card
                FireTypes.SERVICES -> R.layout.service_card_grid_large
                FireTypes.REVIEW_TEMPLATES -> R.layout.coach_review_single_question_card
                else -> R.layout.sport_card_list_medium
            }
        }
    }

}









