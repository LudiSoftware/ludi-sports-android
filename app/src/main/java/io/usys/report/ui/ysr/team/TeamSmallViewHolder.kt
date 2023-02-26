package io.usys.report.ui.ysr.team


import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.loadInRealmList
import io.usys.report.realm.loadInRealmListGridArrangable
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.TeamRef
import io.usys.report.realm.sessionTeams
import io.usys.report.utils.*
import io.usys.report.utils.views.loadUriIntoImgView

/**
 * SPORT LIST VIEW CONTROLS
 */


fun RecyclerView?.setupTeamListFromSession(onClickReturnViewRealmObject: ((View, Team) -> Unit)?, isRef:Boolean = false) {
    sessionTeams {
        this?.loadInRealmList(it, FireTypes.TEAMS, onClickReturnViewRealmObject, isRef)
    }
}

class TeamSmallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var txtItemTeamName = itemView.bindTextView(R.id.cardTeamSmallTxtName)
    var txtItemTeamOrg = itemView.bindTextView(R.id.cardTeamSmallTxtOrg)
    var txtItemTeamEmail = itemView.bindTextView(R.id.cardTeamSmallTxtEmail)
    var imgTeamProfile = itemView.bind<ImageView>(R.id.cardTeamSmallImgProfile)

    fun bind(team: Team?) {
        team?.let {
            txtItemTeamName?.text = it.name
            txtItemTeamOrg?.text = it.headCoachName
            txtItemTeamEmail?.text = it.ageGroup
            val url = it.imgUrl.toString()
            imgTeamProfile.loadUriIntoImgView(url)
        }
    }
    fun bindRef(team: TeamRef?) {
        team?.let {
            txtItemTeamName?.text = it.name
            txtItemTeamOrg?.text = it.headCoachName
            txtItemTeamEmail?.text = it.ageGroup
            val url = it.imgUrl.toString()
            imgTeamProfile.loadUriIntoImgView(url)
        }
    }
}

class TeamLargeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var txtItemTeamName = itemView.bindTextView(R.id.cardTeamSmallTxtName)
    var txtItemTeamOrg = itemView.bindTextView(R.id.cardTeamSmallTxtOrg)
    var txtItemTeamEmail = itemView.bindTextView(R.id.cardTeamSmallTxtEmail)
    var imgTeamProfile = itemView.bind<ImageView>(R.id.cardTeamSmallImgProfile)

    fun bind(team: Team?) {
        team?.let {
            txtItemTeamName?.text = it.name
            txtItemTeamOrg?.text = it.headCoachName
            txtItemTeamEmail?.text = it.ageGroup
            val url = it.imgUrl.toString()
            imgTeamProfile.loadUriIntoImgView(url)
        }
    }

    fun bindRef(team: TeamRef?) {
        team?.let {
            txtItemTeamName?.text = it.name
            txtItemTeamOrg?.text = it.headCoachName
            txtItemTeamEmail?.text = it.ageGroup
            val url = it.imgUrl.toString()
            imgTeamProfile.loadUriIntoImgView(url)
        }
    }
}