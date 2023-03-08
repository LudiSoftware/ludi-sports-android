package io.usys.report.ui.views.listAdapters

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.realm.model.CustomAttribute
import io.usys.report.ui.setOnDoubleClickListener
import io.usys.report.ui.views.addAttribute
import io.usys.report.utils.*
import kotlin.collections.isNullOrEmpty


fun RecyclerView.loadInCustomAttributes(realmList: RealmList<CustomAttribute>?) : CustomAttributesListAdapter? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = CustomAttributesListAdapter(realmList)
    this.layoutManager = linearLayoutManager(this.context)
    this.adapter = adapter
    return adapter
}

class CustomAttributesListAdapter(var attributes: RealmList<CustomAttribute>? = null) : RecyclerView.Adapter<CustomAttributeViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAttributeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ysr_item_field_value, parent, false)
        return CustomAttributeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomAttributeViewHolder, position: Int) {
        attributes?.let { attrs ->
            val attribute = attrs[position]
            holder.bind(attribute)
        }
    }

    override fun getItemCount(): Int {
        return attributes?.size ?: 0
    }

    fun setAttributeList(attributes: RealmList<CustomAttribute>) {
        this.attributes?.clear()
        this.attributes = attributes
        this.notifyDataSetChanged()
    }

    fun addAttribute(key:String, value:String) {
        attributes?.addAttribute(key, value)
        this.notifyItemInserted(attributes?.size?: 0)
    }

}

class CustomAttributeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            if (customAttribute.key!!.contains("id", true)) {
                itemView.removeItemViewFromList()
                return
            }
            if (customAttribute.key!!.contains("imgUrl", false)) {
                itemView.removeItemViewFromList()
                return
            }
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