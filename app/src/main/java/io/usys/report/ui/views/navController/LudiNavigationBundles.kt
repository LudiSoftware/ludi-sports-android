package io.usys.report.ui.views.navController

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import io.realm.RealmObject
import io.usys.report.ui.fragments.YsrFragment

fun Fragment.unbundleRealmObject(): RealmObject? {
    return arguments?.get(YsrFragment.ARG) as? RealmObject
}

fun Fragment.unbundleStringId(): String? {
    return arguments?.getString(YsrFragment.ARG)
}

fun Fragment.unbundleTeamId(): String? {
    return arguments?.getString(YsrFragment.TEAM_ID)
}
fun Fragment.unbundleType(): String? {
    return arguments?.getString(YsrFragment.TYPE)
}
fun Fragment.unbundlePlayerId(): String? {
    return arguments?.getString(YsrFragment.PLAYER_ID)
}

fun Fragment.unbundleOrgId(): String? {
    return arguments?.getString(YsrFragment.ORG_ID)
}
fun Fragment.unbundleRosterId(): String? {
    return arguments?.getString(YsrFragment.ROSTER_ID)
}
fun bundleRealmObject(obj: RealmObject): Bundle {
    return bundleOf(YsrFragment.ARG to obj)
}

fun bundleStringId(obj: String): Bundle {
    return bundleOf(YsrFragment.ARG to obj)
}

fun bundleStringIds(teamId: String?=null, playerId: String?=null, orgId: String?=null, rosterId: String?=null, type: String?=null): Bundle {
    //teamId, playerId, orgId
    return bundleOf(YsrFragment.TEAM_ID to teamId, YsrFragment.PLAYER_ID to playerId, YsrFragment.ORG_ID to orgId, YsrFragment.ROSTER_ID to rosterId, YsrFragment.TYPE to type)
}

fun bundleRosterIds(teamId: String?=null, rosterId: String?=null, type: String?=null): Bundle {
    //teamId, playerId, orgId
    return bundleOf(YsrFragment.TEAM_ID to teamId, YsrFragment.ROSTER_ID to rosterId, YsrFragment.TYPE to type)
}