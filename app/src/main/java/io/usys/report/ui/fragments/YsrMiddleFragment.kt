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

abstract class YsrMiddleFragment : Fragment() {

    companion object {
        val ARG = "realmObj"
    }

    lateinit var rootView : View
    var itemOnClick: ((View, RealmObject) -> Unit)? = null

    var user: User? = null

    var realmInstance: Realm? = null
    var realmObjectArg: RealmObject? = null

    abstract fun setupOnClickListeners()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realmInstance = realm()
        realmInstance?.userOrLogout(requireActivity()) { user = it }
        realmObjectArg = unbundleRealmObject()
    }


}
