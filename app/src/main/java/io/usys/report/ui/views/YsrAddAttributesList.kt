package io.usys.report.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.realm.loadInRealmList
import io.usys.report.realm.model.CustomAttribute
import io.usys.report.utils.*

class YsrAddAttributesList(context: Context) : CardView(context) {
    constructor(context: Context, attrs: AttributeSet) : this(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context)

    var attributeList: RealmList<CustomAttribute>? = null

    var recyclerView: RecyclerView? = null

    override fun onViewAdded(child: View?) {
        bindChildren()
    }

    private fun bindChildren() {
        recyclerView = this.rootView.bind(R.id.ysrRecyclerAddAttribute)
    }

    fun loadInRealmList(realmObjectList: RealmList<RealmObject>, type: String, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        recyclerView?.loadInRealmList(realmObjectList, type, onClickReturnViewRealmObject)
    }


}