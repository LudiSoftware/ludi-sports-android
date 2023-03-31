package io.usys.report.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.realm.model.Sport
import io.usys.report.ui.ludi.note.setupPlayerNoteList
import io.usys.report.ui.ludi.note.setupTeamNoteList
import io.usys.report.ui.ludi.organization.setupOrganizationList
import io.usys.report.ui.ludi.player.setupPlayerListFromTeamSession
import io.usys.report.ui.ludi.player.setupPlayerListGridFromRosterId
import io.usys.report.ui.ludi.service.setupServiceList
import io.usys.report.ui.ludi.sport.setupSportList
import io.usys.report.ui.ludi.team.viewholders.setupTeamListFromSession
import io.usys.report.ui.views.listAdapters.RealmListAdapter
import io.usys.report.ui.views.listAdapters.loadInRealmListArrangable
import io.usys.report.ui.views.listAdapters.loadInRealmListGridArrangable
import io.usys.report.ui.views.listAdapters.setupRosterGridArrangable
import io.usys.report.ui.views.touchAdapters.RealmListTouchAdapter
import io.usys.report.utils.*

class LudiRosterView(context: Context) : RecyclerView(context) {
    constructor(context: Context, attrs: AttributeSet) : this(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context)

    var ludiConstraintLayout: ConstraintLayout? = null

    var realmListAdapter: RealmListAdapter<RealmObject>? = null
    var realmListOnClick: ((View, RealmObject) -> Unit)? = null


    /** RecyclerView/List Functions **/
    fun setupRealmListAdapter(realmList: RealmList<RealmObject>?) {
        val adapter = RealmListAdapter(realmList, "type", realmListOnClick, "small")
        this.layoutManager = GridLayoutManager(this.context, 2)
        this.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(RealmListTouchAdapter(adapter))
        itemTouchHelper.attachToRecyclerView(this)
    }

    fun loadInRealmListArrangable(realmObjectList: RealmList<RealmObject>, type: String, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?, size: String = "string") {
        this.loadInRealmListArrangable(realmObjectList, type, onClickReturnViewRealmObject)
    }

    /** Master Roster List Setup Helper Functions **/
    fun setupRoster(rosterId:String, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        this.setupRosterGridArrangable(rosterId, onClickReturnViewRealmObject)
    }
    fun setupPlayerListOfficialRoster(id:String, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        this.setupPlayerListGridFromRosterId(id, onClickReturnViewRealmObject)
    }
    fun setupPlayerListTeamSession(id:String?, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        if (id == null) {
            return
        }
        this.setupPlayerListFromTeamSession(id, onClickReturnViewRealmObject)
    }
}


class RealmClickListeners<T> {
    var realmListOnClick: ((View, T) -> Unit)? = null
}