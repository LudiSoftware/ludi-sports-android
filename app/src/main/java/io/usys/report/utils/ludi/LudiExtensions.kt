package io.usys.report.utils.ludi

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.usys.report.ui.views.LudiLinearLayout
import io.usys.report.ui.views.viewGroup.LudiViewGroup


class LudiViewGroupViewModel : ViewModel() {
    private val _ludiViewGroup = MutableLiveData<LudiViewGroup>()
    val ludiViewGroup: LiveData<LudiViewGroup> = _ludiViewGroup

    fun setLudiViewGroup(ludiViewGroup: LudiViewGroup) {
        _ludiViewGroup.value = ludiViewGroup
    }
}

fun LudiLinearLayout?.addLudiViewGroup(parent: Fragment, fragments: MutableList<Pair<String, Fragment>>, teamId: String?=null, playerId: String? = null, type: String? = null, headerView: View?=null): LudiViewGroup? {
    if (this == null) return null
    val lvg = LudiViewGroup(parent, this, teamId, playerId, null, type)
    lvg.headerView = headerView
    lvg.setupLudiTabs(fragments)
    return lvg
}

fun LudiLinearLayout?.addLudiRosterViewGroup(parent: Fragment, teamId: String): LudiViewGroup? {
    if (this == null) return null
    val lvg = LudiViewGroup(parent, this, teamId, null, null, null)
    lvg.setupRosterTabs()
    return lvg
}