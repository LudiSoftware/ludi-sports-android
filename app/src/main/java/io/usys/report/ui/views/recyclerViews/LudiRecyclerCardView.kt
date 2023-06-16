package io.usys.report.ui.views.recyclerViews

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.realm.model.Sport
import io.usys.report.realm.realm
import io.usys.report.ui.ludi.note.setupPlayerNoteList
import io.usys.report.ui.ludi.note.setupTeamNoteList
import io.usys.report.ui.ludi.organization.setupOrganizationList
import io.usys.report.ui.ludi.player.setupPlayerListFromTeamSession
import io.usys.report.ui.ludi.player.setupPlayerListGridFromRosterId
import io.usys.report.ui.ludi.service.setupServiceList
import io.usys.report.ui.ludi.sport.setupSportList
import io.usys.report.ui.views.listAdapters.teamLiveList.setupTeamListFromSession
import io.usys.report.ui.views.listAdapters.realmList.RealmListAdapter
import io.usys.report.ui.views.listAdapters.realmList.loadInRealmListArrangable
import io.usys.report.utils.views.bind
import io.usys.report.utils.views.makeGone
import org.jetbrains.anko.backgroundColor


typealias LudiRCVs = MutableMap<String, LudiRecyclerCardView?>
fun emptyLudiRCVs(): LudiRCVs = mutableMapOf()

inline fun ViewGroup.addNewLudiRecyclerCardView(block: (LudiRecyclerCardView?) -> Unit) {
    val lrcv = LudiRecyclerCardView.inflate(this).apply {
        block(this)
    }
    this.addView(lrcv)
}
inline fun LinearLayout.addLudiRecyclerView(block : (LudiRecyclerCardView?) -> Unit) : LudiRecyclerCardView? {
    val lrcv = LudiRecyclerCardView.inflate(this@addLudiRecyclerView).apply {
        // For LinearLayout, just need to set the width and height
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        this@apply?.id = View.generateViewId()  // generate a new id for the view
        this@apply?.layoutParams = layoutParams
        this@apply?.parentLayout = this@addLudiRecyclerView
        block(this@apply)
    }
    this@addLudiRecyclerView.addView(lrcv)
    return lrcv
}


class LudiRecyclerCardView(context: Context) : CardView(context) {
    constructor(context: Context, attrs: AttributeSet) : this(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context)

    companion object {

        private const val LUDI_RECYCLER_VIEW_LAYOUT = R.layout.ludi_recycler_card_view
        fun inflate(vg: ViewGroup) : LudiRecyclerCardView? {
            return LayoutInflater.from(vg.context).inflate(LUDI_RECYCLER_VIEW_LAYOUT, vg, false) as? LudiRecyclerCardView
        }
    }

    var parentLayout: ViewGroup? = null
    val realmInstance = realm()
    var txtTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var adapter: RecyclerView.Adapter<*>? = null

    override fun onViewAdded(child: View?) {
        makeBackgroundTransparent()
        bindChildren()
    }

    private fun bindChildren() {
        txtTitle = this.rootView.bind(R.id.ysrTxtTitle)
        recyclerView = this.rootView.bind(R.id.ysrRecycler)
    }

    fun removeFromParentLayout() {
        parentLayout?.removeView(this)
    }

    /** Adapter **/
    fun loadInAdapter(adapter: RecyclerView.Adapter<*>) {
        this.adapter = adapter
    }
    fun setAdapter() {
        recyclerView?.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reloadAdapter() {
        adapter?.notifyDataSetChanged()
    }

    /** Title **/
    fun setTitle(title:String) {
        txtTitle?.text = title
    }

    fun showTitle() {
        txtTitle?.visibility = View.VISIBLE
    }

    fun hideTitle() {
        txtTitle?.makeGone()
    }

    fun makeBackgroundTransparent() {
        this.backgroundColor = context.getColor(R.color.transparent)
    }

    /** Vertical
     * LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
     *      - Context, Orientation, ReverseLayout
     * LinearLayoutManager.VERTICAL
     * LinearLayoutManager.HORIZONTAL
     * **/
    fun setupVertical() {
        recyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }
    fun setupHorizontal() {
        recyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    /** Grid
     * GridLayoutManager(context, columnCount)
     *      - Context, ColumnCount
     * **/
    fun setupGridVertical(columnCount:Int=2) {
        recyclerView?.layoutManager = GridLayoutManager(context, columnCount, GridLayoutManager.VERTICAL, false)
    }
    fun setupGridHorizontal(columnCount:Int=2) {
        recyclerView?.layoutManager = GridLayoutManager(context, columnCount, GridLayoutManager.HORIZONTAL, false)
    }


    /** RecyclerView/List Functions **/


    /** Master List Setup Helper Functions **/
    fun setupSportList(onClickReturnViewRealmObject: ((View, Sport) -> Unit)?) {
        setTitle("Sports")
        recyclerView?.setupSportList(onClickReturnViewRealmObject)
    }
    fun setupTeamNotesList(ownerId:String, teamId: String?, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        if (teamId == null) {
            setTitle("Uh Oh! No Data Found.")
            return
        }
        setTitle("Team Notes")
        recyclerView?.setupTeamNoteList(ownerId, teamId, onClickReturnViewRealmObject)
    }
    fun setupPlayerNotesList(ownerId:String, playerId: String?, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        if (playerId == null) {
            setTitle("Uh Oh! No Data Found.")
            return
        }
        setTitle("Player Notes")
        recyclerView?.setupPlayerNoteList(ownerId, playerId, onClickReturnViewRealmObject)
    }
}