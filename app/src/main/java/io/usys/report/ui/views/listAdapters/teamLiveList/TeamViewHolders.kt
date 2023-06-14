package io.usys.report.ui.views.listAdapters.teamLiveList


import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.findTeamById
import io.usys.report.realm.model.Team
import io.usys.report.realm.realm
import io.usys.report.realm.sessionTeams
import io.usys.report.ui.views.listAdapters.realmList.loadInRealmList
import io.usys.report.ui.views.navController.toTeamProfileVG
import io.usys.report.utils.*
import io.usys.report.utils.views.bind
import io.usys.report.utils.views.bindTextView
import io.usys.report.utils.views.loadUriIntoImgView

/**
 * TEAM LIST VIEW CONTROLS
 */

fun RecyclerView?.setupTeamListFromSession(onClickReturnViewRealmObject: ((View, Team) -> Unit)?, size:String = "small") {
    sessionTeams {
        this?.loadInRealmList(it, FireTypes.TEAMS, onClickReturnViewRealmObject, size)
    }
}

fun RecyclerView?.setupTeamListFromIds(teamIds:MutableList<String>, onClickReturnViewRealmObject: ((View, Team) -> Unit)?, size:String = "small") {
    val tempList = RealmList<Team>()
    val realm = realm()
    teamIds.forEach {
        realm.findTeamById(it)?.let { team ->
            tempList.add(team)
        }
    }
    if (tempList.isNotEmpty()) {
        this?.loadInRealmList(tempList, FireTypes.TEAMS, onClickReturnViewRealmObject, size)
    }
}

class TeamSmallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var txtItemTeamName = itemView.bindTextView(R.id.cardTeamSmallTxtName)
    var txtItemTeamOrg = itemView.bindTextView(R.id.cardTeamSmallTxtOrg)
    var txtItemTeamEmail = itemView.bindTextView(R.id.cardTeamSmallTxtEmail)
    var imgTeamProfile = itemView.bind<CircleImageView>(R.id.cardTeamSmallImgProfile)

    fun bind(team: Team?, fragment: Fragment?) {
        var teamId = team?.id ?: "UNKNOWN"
        team?.let {
            teamId = it.id
            txtItemTeamName?.text = it.name
            txtItemTeamOrg?.text = it.headCoachName
            txtItemTeamEmail?.text = it.ageGroup
            val url = it.imgUrl.toString()
            imgTeamProfile?.loadUriIntoImgView(url)
        }

        itemView.setOnClickListener {
            log("Team Clicked")
            fragment?.toTeamProfileVG(teamId)
        }
    }
}

class TeamLargeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var txtItemTeamName = itemView.bindTextView(R.id.cardTeamSmallTxtName)
    var txtItemTeamOrg = itemView.bindTextView(R.id.cardTeamSmallTxtOrg)
    var txtItemTeamEmail = itemView.bindTextView(R.id.cardTeamSmallTxtEmail)
    var imgTeamProfile = itemView.bind<CircleImageView>(R.id.cardTeamSmallImgProfile)

    fun bind(team: Team?) {
        team?.let {
            txtItemTeamName?.text = it.name
            txtItemTeamOrg?.text = it.headCoachName
            txtItemTeamEmail?.text = it.ageGroup
            val url = it.imgUrl.toString()
            imgTeamProfile?.loadUriIntoImgView(url)

            itemView.setOnClickListener {
                log("Team Clicked")
            }
        }
    }


}