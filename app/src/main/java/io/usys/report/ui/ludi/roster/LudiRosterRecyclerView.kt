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

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return super.onInterceptTouchEvent(e)
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


