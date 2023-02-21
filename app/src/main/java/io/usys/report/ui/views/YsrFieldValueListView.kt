package io.usys.report.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.*
import io.usys.report.realm.loadInRealmList
import io.usys.report.realm.model.Coach
import io.usys.report.realm.model.Sport
import io.usys.report.ui.onClickReturnStringString
import io.usys.report.ui.onClickReturnViewT
import io.usys.report.ui.ysr.organization.setupOrganizationList
import io.usys.report.ui.ysr.player.setupPlayerListFromSession
import io.usys.report.ui.ysr.service.setupServiceList
import io.usys.report.ui.ysr.sport.setupSportList
import io.usys.report.ui.ysr.team.setupTeamListFromSession
import io.usys.report.utils.*
import io.usys.report.utils.views.loadUriIntoImgView
import java.util.*

class YsrFieldValueListView(context: Context) : CardView(context) {
    constructor(context: Context, attrs: AttributeSet) : this(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context)

    var txtTitle: TextView? = null
    var recyclerView: RecyclerView? = null

    override fun onViewAdded(child: View?) {
        bindChildren()
    }

    private fun bindChildren() {
        recyclerView = this.rootView.bind(R.id.ysrFieldValueRecycler)
    }

    fun loadInRealmObject(realmObject: RealmObject, type:String) {
        recyclerView?.loadInRealmObject(realmObject, type)
    }

}

fun RecyclerView.loadInRealmObject(realmObject: RealmObject?, type: String) : YsrFieldValueListAdapter<RealmObject>? {
    if (realmObject.isNullOrEmpty()) return null
    val adapter = YsrFieldValueListAdapter(realmObject, type)
    this.layoutManager = linearLayoutManager(this.context)
    this.adapter = adapter
    return adapter
}

open class YsrFieldValueListAdapter<T: RealmObject>(var realmObject: T?, var type: String) :
    RecyclerView.Adapter<FieldValueViewHolder>() {

    var fieldNames: MutableSet<String>? = mutableSetOf()

    init {
        val schema = realm().schema.get(type)
        fieldNames = schema?.fieldNames
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldValueViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.ysr_item_field_value, parent, false)
        return FieldValueViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return fieldNames?.size ?: 0
    }

    override fun onBindViewHolder(holder: FieldValueViewHolder, position: Int) {
        println("binding realmlist")

        fieldNames?.let {
            val key = it.elementAt(position)
            val value = realmObject?.getValue<RealmObject, String>(key, "null")
            if (key == "id" || value is String && value == "null") {
                // remove item completely.
                holder.bind(key, null)
            } else {
                holder.bind(key, value.toString())
            }
        }
    }

}

fun String.capitalizeFirstChar(): String {
    if (this.isEmpty()) {
        return this
    }
    return this.substring(0, 1).toUpperCase(Locale.ROOT) + this.substring(1)
}

class FieldValueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var txtFieldName = itemView.bindTextView(R.id.ysrTxtItemFieldName)
    var editValue = itemView.bind<EditText>(R.id.ysrEditItemFieldValue)

    fun bind(key: String, value: String?) {
        if (value != null) {
            txtFieldName?.text = key.capitalizeFirstChar()
            editValue.setText(value)
        } else {
            // Set the height of the view holder to 0
            itemView.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 0)
            // Set the margin of the view holder to 0
            val marginLayoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.setMargins(0, 0, 0, 0)
        }
    }
}