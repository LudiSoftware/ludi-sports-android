package io.usys.report.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmObjectSchema
import io.realm.RealmSchema
import io.usys.report.R
import io.usys.report.realm.*
import io.usys.report.realm.model.CustomAttribute
import io.usys.report.utils.*
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
    var avoidFieldList: MutableList<String> = mutableListOf()

    init {
        fieldNames = getRealmFields(type)
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
            val value = realmObject?.getValue(key, "null")

            if (avoidFieldList.isNotEmpty()) {
                if (avoidFieldList.contains(key)) {
                    holder.bind(key, null)
                    return@let
                }
            }

            if (key == "id" || value is String && value == "null") {
                // remove item completely.
                holder.bind(key, null)
            } else {
                holder.bind(key, value.toString())
            }
        }
    }

    // Add field names to avoid in the list
    fun addAvoidFieldList(vararg fieldNames: String) {
        avoidFieldList.addAll(fieldNames)
    }

}

class CustomAttributesListAdapter(private val attributes: RealmList<CustomAttribute>?) :
    RecyclerView.Adapter<FieldValueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldValueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ysr_item_attribute, parent, false)
        return FieldValueViewHolder(view)
    }

    override fun onBindViewHolder(holder: FieldValueViewHolder, position: Int) {
        attributes?.let { attrs ->
            val attribute = attrs[position]
            holder.bind(attribute)
        }
    }

    override fun getItemCount(): Int {
        return attributes?.size ?: 0
    }

}

class FieldValueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var txtFieldName = itemView.bindTextView(R.id.ysrTxtItemFieldName)
    var editValue = itemView.bind<EditText>(R.id.ysrEditItemFieldValue)

    fun bind(key: String, value: String?) {
        if (value != null) {
            txtFieldName?.text = key.capitalizeFirstChar()
            editValue.setText(value)
        } else {
            itemView.removeItemViewFromList()
        }
    }

    fun bind(customAttribute: CustomAttribute?) {
        if (customAttribute != null) {
            txtFieldName?.text = customAttribute.key?.capitalizeFirstChar()
            editValue.setText(customAttribute.value)
        } else {
            itemView.removeItemViewFromList()
        }
    }
}

fun View.removeItemViewFromList() {
    // Set the height of the view holder to 0
    this.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 0)
    // Set the margin of the view holder to 0
    val marginLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    marginLayoutParams.setMargins(0, 0, 0, 0)
}