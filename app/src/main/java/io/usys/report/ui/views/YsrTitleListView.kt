package io.usys.report.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.ui.views.listAdapters.loadInCustomAttributes
import io.usys.report.realm.model.Sport
import io.usys.report.ui.ludi.note.setupPlayerNoteList
import io.usys.report.ui.ludi.note.setupTeamNoteList
import io.usys.report.ui.ludi.organization.setupOrganizationList
import io.usys.report.ui.ludi.player.setupPlayerListFromTeamSession
import io.usys.report.ui.ludi.player.setupPlayerListGridFromRosterId
import io.usys.report.ui.ludi.service.setupServiceList
import io.usys.report.ui.ludi.sport.setupSportList
import io.usys.report.ui.ludi.team.viewholders.setupTeamListFromSession
import io.usys.report.utils.*

class YsrTitleListView(context: Context) : CardView(context) {
    constructor(context: Context, attrs: AttributeSet) : this(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context)

    var txtTitle: TextView? = null
    var recyclerView: RecyclerView? = null

    override fun onViewAdded(child: View?) {
        bindChildren()
    }

    private fun bindChildren() {
        txtTitle = this.rootView.bind(R.id.ysrTxtTitle)
        recyclerView = this.rootView.bind(R.id.ysrRecycler)

    }

    fun setTitle(title:String) {
        txtTitle?.text = title
    }

    fun loadInRealmList(realmObjectList: RealmList<RealmObject>, type: String, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?, size: String = "string") {
        recyclerView?.loadInCustomAttributes(realmObjectList, type, onClickReturnViewRealmObject, size)
    }

    fun setupSportList(onClickReturnViewRealmObject: ((View, Sport) -> Unit)?) {
        setTitle("Sports")
        recyclerView?.setupSportList(onClickReturnViewRealmObject)
    }

    fun setupServiceList(onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        setTitle("Services")
        recyclerView?.setupServiceList(context, onClickReturnViewRealmObject)
    }

    fun setupOrganizationList(realmObjectArg: RealmObject?, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        setTitle("Organizations")
        recyclerView?.setupOrganizationList(context, realmObjectArg, onClickReturnViewRealmObject)
    }

    fun setupTeamRefListFromSession(onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?, size:String = "small") {
        setTitle("Teams")
        recyclerView?.setupTeamListFromSession(onClickReturnViewRealmObject, size)
    }

    fun setupPlayerListOfficialRoster(id:String, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        setTitle("Official Roster")
        recyclerView?.setupPlayerListGridFromRosterId(id, onClickReturnViewRealmObject)
    }
    fun setupPlayerListTeamSession(id:String?, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        if (id == null) {
            setTitle("Uh Oh! No Data Found.")
            return
        }
        setTitle("Session Roster")
        recyclerView?.setupPlayerListFromTeamSession(id, onClickReturnViewRealmObject)
    }
    fun setupTeamNotesList(teamId: String?, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        if (teamId == null) {
            setTitle("Uh Oh! No Data Found.")
            return
        }
        setTitle("Team Notes")
        recyclerView?.setupTeamNoteList(teamId, onClickReturnViewRealmObject)
    }

    fun setupPlayerNotesList(playerId: String?, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        if (playerId == null) {
            setTitle("Uh Oh! No Data Found.")
            return
        }
        setTitle("Player Notes")
        recyclerView?.setupPlayerNoteList(playerId, onClickReturnViewRealmObject)
    }
}