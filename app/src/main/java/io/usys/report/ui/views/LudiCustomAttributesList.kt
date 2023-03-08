package io.usys.report.ui.views

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.ui.views.listAdapters.linearLayoutManager
import io.usys.report.ui.views.listAdapters.loadInRealmList
import io.usys.report.realm.model.CustomAttribute
import io.usys.report.ui.setOnDoubleClickListener
import io.usys.report.ui.views.listAdapters.CustomAttributesListAdapter
import io.usys.report.ui.views.listAdapters.loadInCustomAttributes
import io.usys.report.utils.*
class LudiCustomAttributesList @JvmOverloads constructor(context: Context,
                                                         attrs: AttributeSet? = null,
                                                         defStyleAttr: Int = 0) : CardView(context, attrs, defStyleAttr) {


    var recyclerView: RecyclerView? = null
    var mview: View? = null
    init {
        LayoutInflater.from(context).inflate(R.layout.ysr_add_attributes, this.rootView as ViewGroup)
    }

    // add any custom methods or properties here
    override fun onViewAdded(child: View?) {
        bindChildren()
    }

    private fun bindChildren() {
        recyclerView = this.rootView?.bind(R.id.ysrRecyclerAddAttribute)
    }

    fun loadInRealmList(realmObjectList: RealmList<CustomAttribute>) {
        recyclerView?.loadInCustomAttributes(realmObjectList)
    }

}
