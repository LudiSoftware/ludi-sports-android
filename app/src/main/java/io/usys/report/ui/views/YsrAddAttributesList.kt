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
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.realm.linearLayoutManager
import io.usys.report.realm.loadInRealmList
import io.usys.report.realm.model.CustomAttribute
import io.usys.report.ui.setOnDoubleClickListener
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

fun RecyclerView.loadInCustomAttributes(realmList: RealmList<CustomAttribute>?) : CustomAttributesListAdapter? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = CustomAttributesListAdapter(realmList)
    this.layoutManager = linearLayoutManager(this.context)
    this.adapter = adapter
    return adapter
}

class CustomAttributesListAdapter(private val attributes: RealmList<CustomAttribute>?) : RecyclerView.Adapter<FieldValueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldValueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ysr_item_field_value, parent, false)
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
            editValue.setOnDoubleClickListener {
                editValue.setEditable(true)
                editValue.setTextColor(Color.BLUE)
                editValue.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        log("onTextChanged: $s")
                    }

                    override fun afterTextChanged(s: Editable?) { log("afterTextChanged: $s")
                    }
                })
                true
            }
            editValue.setEditable(false)

        } else {
            itemView.removeItemViewFromList()
        }
    }
}

fun EditText.setEditable(isEditable: Boolean) {
    this.isFocusable = isEditable
    this.isCursorVisible = isEditable
    this.isFocusableInTouchMode = isEditable
    this.isEnabled = true
    this.isClickable = true
}


fun View.removeItemViewFromList() {
    // Set the height of the view holder to 0
    this.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 0)
    // Set the margin of the view holder to 0
    val marginLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    marginLayoutParams.setMargins(0, 0, 0, 0)
}