package io.usys.report.ui.views.cardViews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.ui.views.listAdapters.loadInCustomAttributes
import io.usys.report.utils.views.bind
import io.usys.report.utils.views.makeGone
import io.usys.report.utils.views.makeVisible

class YsrCreateListView(context: Context) : CardView(context) {
    constructor(context: Context, attrs: AttributeSet) : this(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context)

    var txtTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var btnCreate: ImageButton? = null
    var createLayout: View? = null

    override fun onViewAdded(child: View?) {
        bindChildren()
    }

    private fun bindChildren() {
        txtTitle = this.rootView.bind(R.id.ysrCreateListTxtTitle)
        recyclerView = this.rootView.bind(R.id.ysrCreateListRecycler)
        btnCreate = this.rootView.bind(R.id.ysrCreateListImgBtnCreate)
        createLayout = this.rootView.bind(R.id.includeYsrCreateListCreateNote)
        createLayout?.makeGone()

        btnCreate?.setOnClickListener {
            toggleCreateLayout()
        }
    }

    fun toggleCreateLayout() {
        if (createLayout!!.isVisible) {
            createLayout?.makeGone()
            createLayout?.isEnabled = false
        } else {
            createLayout?.makeVisible()
            createLayout?.isEnabled = true
        }
    }

    fun setTitle(title:String) {
        txtTitle?.text = title
    }

    fun loadInRealmList(realmObjectList: RealmList<RealmObject>, type: String, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        recyclerView?.loadInCustomAttributes(realmObjectList, type, onClickReturnViewRealmObject)
    }



}