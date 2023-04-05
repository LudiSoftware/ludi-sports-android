package io.usys.report.ui.ludi.roster

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import io.realm.RealmObject
import io.usys.report.ui.ludi.player.setupPlayerListFromTeamSession
import io.usys.report.ui.ludi.player.setupPlayerListGridFromRosterId
import io.usys.report.ui.views.LudiViewGroupViewModel

class LudiRosterRecyclerView(context: Context) : RecyclerView(context) {
    constructor(context: Context, attrs: AttributeSet) : this(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context)

    private var activity: ViewModelStoreOwner? = null
    private lateinit var ludiViewGroupViewModel: LudiViewGroupViewModel
    private var viewPager2: ViewPager2? = null
    private var tabLayout: TabLayout? = null

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return super.onInterceptTouchEvent(e)
    }

    fun setActivity(activity: ViewModelStoreOwner) {
        this.activity = activity
        ludiViewGroupViewModel = ViewModelProvider(this.activity!!)[LudiViewGroupViewModel::class.java]
        ludiViewGroupViewModel.ludiViewGroup.value?.tabLayout?.let {
            tabLayout = it
            tabLayout?.setOnClickListener(null)
        }
        ludiViewGroupViewModel.ludiViewGroup.value?.viewPager?.let {
            viewPager2 = it
            viewPager2?.setOnClickListener(null)
        }
    }

    /** Master Roster List Setup Helper Functions **/
    fun setupRoster(rosterId:String) {
        this.setupRosterGridArrangable(rosterId)
    }
    fun setupPlayerListOfficialRoster(id:String, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        this.setupPlayerListGridFromRosterId(id, onClickReturnViewRealmObject)
    }
    fun setupPlayerListTeamSession(id:String?, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
        if (id == null) {
            return
        }
        this.setupPlayerListFromTeamSession(id, onClickReturnViewRealmObject)
    }
}


