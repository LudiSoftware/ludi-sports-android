package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.model.Sport
import io.usys.report.model.User
import io.usys.report.ui.fragments.YsrFragment.Companion.ARG
import io.usys.report.utils.userOrLogout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Created by ChazzCoin : October 2022.
 * * Make sure to add any new fragments to Navigation XML! * *
 */

abstract class YsrFragment : Fragment() {

    companion object {
        val ARG = "realmObj"
    }

    lateinit var rootView : View
    var itemOnClick: ((View, RealmObject) -> Unit)? = null
    var user: User? = null
    val main = CoroutineScope(Dispatchers.Main + SupervisorJob())

    var sportList : RealmList<Sport> = RealmList()
    var realmObjectArg: RealmObject? = null

    abstract fun setupOnClickListeners()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userOrLogout(requireActivity()) { user = it }
        realmObjectArg = unbundleRealmObject()
    }

}

fun Fragment.toFragment(fragId: Int, bundle: Bundle = bundleOf()) {
    this.findNavController().navigate(fragId, bundle)
}

fun Fragment.toFragment(fragId: Int, realmObject: RealmObject) {
    this.findNavController().navigate(fragId, bundleRealmObject(realmObject))
}

fun Fragment.unbundleRealmObject(): RealmObject? {
    return arguments?.get(ARG) as? RealmObject
}

fun bundleRealmObject(obj: RealmObject): Bundle {
    return bundleOf(ARG to obj)
}

fun Fragment.getArg(argName: String): String? {
    return arguments?.getString(argName)
}