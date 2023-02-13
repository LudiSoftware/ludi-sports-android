package io.usys.report.ui.ysr.team


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.model.Team
import io.usys.report.utils.*

/**
 * SPORT LIST VIEW CONTROLS
 */


fun RecyclerView?.setupTeamListFromSession(onClickReturnViewRealmObject: ((View, Team) -> Unit)?) {
    // Load Organizations by Sport

    val rv = this
    sessionTeams {
        rv?.loadInRealmListGridArrangable(it, FireTypes.TEAMS, onClickReturnViewRealmObject)
        return
    }
}

class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var txtItemTeamName = itemView.bindTextView(R.id.cardTeamTinyTxtName)

    fun bind(team: Team?) {
        team?.let { txtItemTeamName?.text = it.name }
    }
}
