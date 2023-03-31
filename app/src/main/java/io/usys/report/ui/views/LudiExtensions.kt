package io.usys.report.ui.views

import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import io.usys.report.ui.views.viewGroup.LudiViewGroup

fun LinearLayout?.addLudiViewGroup(parent: Fragment, fragments: MutableList<Pair<String, Fragment>>, teamId: String?=null, playerId: String? = null, type: String? = null) {
    if (this == null) return
    val lvg = LudiViewGroup(parent, this, teamId, playerId, null, type)
    lvg.setupLudiTabs(fragments)
}

fun LinearLayout?.addLudiRosterViewGroup(parent: Fragment, teamId: String) {
    if (this == null) return
    val lvg = LudiViewGroup(parent, this, teamId, null, null, null)
    lvg.setupRosterTabs()
}