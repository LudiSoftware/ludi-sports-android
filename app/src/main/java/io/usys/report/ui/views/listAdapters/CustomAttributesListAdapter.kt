package io.usys.report.ui.views.listAdapters

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.realm.model.CustomAttribute
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.toCustomAttributesList
import io.usys.report.realm.model.toPlayerRef
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

class CustomAttributesListAdapter() : RecyclerView.Adapter<CustomAttributeViewHolder>() {

    var _MODE = 2
    var EDIT = 1
    var DISPLAY = 2
    var id: String? = null
    var type: String? = null
    var attributes: RealmList<CustomAttribute>? = null
    var editTexts = mutableListOf<EditText>()
    var fieldTexts = mutableListOf<EditText>()

    constructor(attributes: RealmList<CustomAttribute>? = null) : this() {
        this.attributes = attributes
    }
    constructor(playerRef: PlayerRef) : this() {
        this.id = playerRef.id
        this.type = "player"
        this.attributes = playerRef.toCustomAttributesList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAttributeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ysr_item_add_attributes, parent, false)
        return CustomAttributeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomAttributeViewHolder, position: Int) {
        attributes?.let { attrs ->
            val customAttribute = attrs[position]

            if (customAttribute?.key!!.contains("id", true)) {
                holder.itemView.removeItemViewFromList()
                return
            }
            if (customAttribute.key!!.contains("imgUrl", false)) {
                holder.itemView.removeItemViewFromList()
                return
            }

            // Field Name Setup
            if (customAttribute.key!! == "Field Name") {
                fieldTexts.add(holder.txtFieldName)
                holder.txtFieldName.setText(customAttribute.key)
                holder.txtFieldName.setOnDoubleClickListener {
                    holder.txtFieldName.setEditable(true)
                }
                holder.txtFieldName.setEditable(false)
            } else {
                holder.txtFieldName.setText(customAttribute.key?.capitalizeFirstChar())
                holder.txtFieldName.setEditable(false)
            }

            // Value Setup
            this.editTexts.add(holder.editValue)
            holder.editValue.setText(customAttribute.value)
            holder.editValue.setOnDoubleClickListener {
                changeModeToEdit()
                holder.editValue.onTextChangeWatcher {
                    log("onTextChanged: $it")
                }
            }
            holder.editValue.setEditable(false)
        } ?: run {
            holder.itemView.removeItemViewFromList()
        }
    }

    fun changeModeToEdit() {
        _MODE = EDIT
        for (et in editTexts) {
            et.setEditable(true)
            et.setTextColor(Color.BLUE)
        }
        for (et in fieldTexts) {
            et.setEditable(true)
            et.setTextColor(Color.BLUE)
        }
    }

    fun changeModeToDisplay() {
        _MODE = DISPLAY
        for (et in editTexts) {
            et.setEditable(false)
            et.setTextColor(Color.BLACK)
        }
        for (et in fieldTexts) {
            et.setEditable(false)
            et.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount(): Int {
        return attributes?.size ?: 0
    }

    fun addEmptyAttribute() {
        attributes?.addAttribute("Field Name", "Add Input Here.")
        this.notifyItemInserted(attributes?.size?: 0)
    }
    fun addAttribute(key:String, value:String) {
        attributes?.addAttribute(key, value)
        this.notifyItemInserted(attributes?.size?: 0)
    }

    fun exportPlayerRef(): PlayerRef? {
        return attributes?.toPlayerRef()
    }
    // todo: Evals

}

inline fun EditText.onTextChangeWatcher(crossinline onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not needed for this example
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(s.toString())
        }
        override fun afterTextChanged(s: Editable?) {
            // Not needed for this example
        }
    })
}


class CustomAttributeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var txtFieldName = itemView.bind<EditText>(R.id.ysrTxtItemFieldName)
    var editValue = itemView.bind<EditText>(R.id.ysrEditItemFieldValue)
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