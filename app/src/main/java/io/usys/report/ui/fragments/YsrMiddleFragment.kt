package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import io.realm.RealmModel
import io.realm.RealmObject
import io.usys.report.realm.model.User
import io.usys.report.realm.model.userOrLogout

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

    var realmObjectArg: RealmObject? = null

    abstract fun setupOnClickListeners()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userOrLogout(requireActivity()) { user = it }
        realmObjectArg = unbundleRealmObject()
    }


}
