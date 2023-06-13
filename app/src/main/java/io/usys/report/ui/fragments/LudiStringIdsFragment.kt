package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import io.realm.Realm
import io.realm.RealmObject
import io.usys.report.realm.model.users.User
import io.usys.report.realm.model.users.userOrLogout
import io.usys.report.realm.realm

/**
 * Created by ChazzCoin : October 2022.
 * * Make sure to add any new fragments to Navigation XML! * *
 */

abstract class LudiStringIdsFragment : Fragment() {

    lateinit var rootView : View
    var itemOnClick: ((View, RealmObject) -> Unit)? = null

    var user: User? = null

    var type: String? = "unassigned"
    var teamId: String? = "unassigned"
    var playerId: String? = "unassigned"
    var orgId: String? = "unassigned"
    var rosterId:String? = "unassigned"

    var realmInstance: Realm? = null
    var realmStringId: String? = "unassigned"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realmInstance = realm()
        realmInstance?.userOrLogout(requireActivity()) { user = it }
        type = unbundleType()
        teamId = unbundleTeamId()
        playerId = unbundlePlayerId()
        orgId = unbundleOrgId()
        rosterId = unbundleRosterId()
    }

}
