package io.usys.report.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmModel
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.realm.model.Sport
import io.usys.report.ui.ysr.organization.setupOrganizationList
import io.usys.report.ui.ysr.service.setupServiceList
import io.usys.report.ui.ysr.sport.setupSportList
import io.usys.report.ui.ysr.team.setupTeamListFromSession
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

    fun setupTeamListFromSession(onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        setTitle("Teams")
        recyclerView?.setupTeamListFromSession(onClickReturnViewRealmObject)
    }

//    fun setupPlayerList(onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
//        recyclerView?.setupSportList(context, onClickReturnViewRealmObject)
//    }

}