package io.usys.report.ui.views.navController

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import io.realm.RealmObject
import io.usys.report.R


const val TO_DASHBOARD = R.id.navigation_dashboard
const val TO_MANAGEMENT = R.id.navigation_management
const val TO_PROFILE = R.id.navigation_profile
const val TO_TEAM_PROFILE = R.id.navigation_team_profile
const val TO_PLAYER_PROFILE = R.id.navigation_player_profile
const val TO_ORG_PROFILE = R.id.navigation_org_profile
const val TO_ROSTER_BUILDER = R.id.navigation_roster_builder_frag
const val TO_FORMATION_BUILDER = R.id.navigation_formation_builder
const val TO_CREATE_NOTE = R.id.navigation_dual_notes


/** Ludi Nav Controller **/
fun AppCompatActivity.ludiNavController() : NavController {
    return findNavController(R.id.nav_host_fragment)
}

/** NAVIGATION **/
fun Fragment.toFragmentWithRealmObject(fragId: Int, bundle: Bundle = bundleOf()) {
    this.findNavController().navigate(fragId, bundle)
}

fun Fragment.toFragmentWithRealmObject(fragId: Int, realmObject: RealmObject) {
    this.findNavController().navigate(fragId, bundleRealmObject(realmObject))
}

fun Fragment.toFragmentWithId(fragId: Int, stringId: String) {
    this.findNavController().navigate(fragId, bundleStringId(stringId))
}

fun Fragment.toFragmentWithIds(fragId: Int, teamId:String?=null, playerId:String?=null, orgId:String?=null, rosterId:String?=null, type:String?=null) {
    this.findNavController().navigate(fragId, bundleStringIds(teamId, playerId, orgId, rosterId, type))
}

fun Fragment.toViewRosterFragment(fragId: Int, teamId:String?=null, rosterId:String, type:String?=null) {
    this.findNavController().navigate(fragId, bundleRosterIds(teamId, rosterId, type))
}

fun Fragment.toTeamProfileVG(teamId: String) {
    toFragmentWithRealmObject(TO_TEAM_PROFILE, bundleStringId(teamId))
}
fun Fragment.toPlayerProfile(playerId:String, teamId:String?=null, rosterId: String?=null) {
    toFragmentWithIds(TO_PLAYER_PROFILE, teamId = teamId, playerId = playerId, rosterId = rosterId)
}

fun Fragment.toFormationBuilder(teamId:String?=null) {
    toFragmentWithIds(TO_FORMATION_BUILDER, teamId = teamId)
}

fun Fragment.toRosterBuilder(teamId:String?=null) {
    toFragmentWithIds(TO_ROSTER_BUILDER, teamId = teamId)
}

fun Fragment.goBack() {
    this.findNavController().navigateUp()
}