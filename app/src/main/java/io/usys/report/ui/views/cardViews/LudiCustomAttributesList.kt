package io.usys.report.ui.views.cardViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.ui.views.listAdapters.loadInCustomAttributes
import io.usys.report.realm.model.CustomAttribute
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.toCustomAttributesList
import io.usys.report.ui.views.listAdapters.CustomAttributesListAdapter
import io.usys.report.utils.*
import io.usys.report.utils.views.animateOnClickListener

class LudiCustomAttributesList @JvmOverloads constructor(context: Context,
                                                         attrs: AttributeSet? = null,
                                                         defStyleAttr: Int = 0) : CardView(context, attrs, defStyleAttr) {
    var type: String = "normal"
    val customAttributesList = RealmList<CustomAttribute>()
    var adapter: CustomAttributesListAdapter? = null
    var btnAdd: ImageButton? = null
    var btnSave: Button? = null
    var btnCancel: Button? = null
    var recyclerView: RecyclerView? = null
    var linearLayoutOptions: LinearLayout? = null
    var constraintLayout: ConstraintLayout? = null
    var cardView: CardView? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.ysr_add_attributes, this.rootView as ViewGroup)
    }

    // add any custom methods or properties here
    override fun onViewAdded(child: View?) {
        bindChildren()
        setupOnClickListeners()
    }

    private fun bindChildren() {
        recyclerView = this.rootView?.bind(R.id.ysrRecyclerAddAttribute)
        btnSave = this.rootView?.bind(R.id.btnAddAttributeSave)
        btnCancel = this.rootView?.bind(R.id.btnAddAttributeCancel)
        btnAdd = this.rootView?.bind(R.id.btnAddAttributeNew)
        linearLayoutOptions = this.rootView?.bind(R.id.ysrLinearLayoutSaveCancel)
        constraintLayout = this.rootView?.bind(R.id.ysrMainLayoutAddAttributeListView)
        cardView = this.rootView?.bind(R.id.ysrAddAttributeListView)
    }

    private fun setupOnClickListeners() {
        btnAdd?.animateOnClickListener {
            log("Add button clicked")
            adapter?.addEmptyAttribute()
            adapter?.changeModeToEdit()
        }
        btnSave?.setOnClickListener {
            log("Save button clicked")
            adapter?.changeModeToDisplay()
        }
        btnCancel?.setOnClickListener {
            log("Cancel button clicked")
        }
    }

    fun loadInPlayerRef(playerRef: PlayerRef) {
        this.type = "player"
        val temp = playerRef.toCustomAttributesList()
        adapter = recyclerView?.loadInCustomAttributes(temp)
    }

    fun loadInCustomAttributes(realmObjectList: RealmList<CustomAttribute>) {
        recyclerView?.loadInCustomAttributes(realmObjectList)
    }

}

fun RealmList<CustomAttribute>.addAttribute(key:String, value:String) {
    this.add(CustomAttribute().apply {
        add(key, value)
    })
}
