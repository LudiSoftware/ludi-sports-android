package io.usys.report.ui.fragments

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FirePaths
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireUploadToStorage
import io.usys.report.realm.model.Sport
import io.usys.report.realm.model.users.User
import io.usys.report.realm.model.users.getUserId
import io.usys.report.realm.model.users.safeUser
import io.usys.report.realm.model.users.userOrLogout
import io.usys.report.realm.realm
import io.usys.report.ui.fragments.YsrFragment.Companion.ARG
import io.usys.report.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Created by ChazzCoin : October 2022.
 * * Make sure to add any new fragments to Navigation XML! * *
 */

abstract class YsrFragment : Fragment() {

    companion object {
        const val ARG = "realmObj"
    }

    lateinit var rootView : View
    lateinit var storage: FirebaseStorage
    var itemOnClick: ((View, RealmObject) -> Unit)? = null
    var pickImageIntent: ActivityResultLauncher<PickVisualMediaRequest>? = null

    var user: User? = null
    var userId: String? = null
    val main = CoroutineScope(Dispatchers.Main + SupervisorJob())

    var realmInstance: Realm? = null
    var sportList : RealmList<Sport> = RealmList()
    var realmObjectArg: RealmObject? = null

    abstract fun setupOnClickListeners()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Request Permissions...
        fairRequestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) { mapOfResults ->
            log(mapOfResults.toString())
        }
        //Create Initial Intent for Uploading Image.
        pickImageIntent = fairGetPickImageFromGalleryIntent { itUri ->
            log(itUri)
            itUri.fireUploadToStorage(requireContext(), FirePaths.PROFILE_IMAGE_PATH_BY_ID(FireTypes.USERS, realmInstance?.getUserId() ?: return@fairGetPickImageFromGalleryIntent))
        }
//        setupMenu()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        realmInstance = realm()
        realmInstance?.safeUser {
            user = it
            userId = it.id
        }
        realmInstance?.userOrLogout(requireActivity()) { user = it }
        storage = Firebase.storage
        realmObjectArg = unbundleRealmObject()
    }


    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.general_top_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_logout -> popAskUserPickImageGallery {
                        launchPickImageIntent()
                    }.show()
                    else -> {}
                }
                return true
            }

        })
    }

    fun launchPickImageIntent(){
        pickImageIntent?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}

fun Fragment.toFragmentWithRealmObject(fragId: Int, bundle: Bundle = bundleOf()) {
    this.findNavController().navigate(fragId, bundle)
}

fun Fragment.toFragmentWithRealmObject(fragId: Int, realmObject: RealmObject) {
    this.findNavController().navigate(fragId, bundleRealmObject(realmObject))
}

fun Fragment.toFragmentWithId(fragId: Int, stringId: String) {
    this.findNavController().navigate(fragId, bundleStringId(stringId))
}

fun Fragment.unbundleRealmObject(): RealmObject? {
    return arguments?.get(ARG) as? RealmObject
}

fun Fragment.unbundleStringId(): String? {
    return arguments?.getString(ARG)
}

fun bundleRealmObject(obj: RealmObject): Bundle {
    return bundleOf(ARG to obj)
}

fun bundleStringId(obj: String): Bundle {
    return bundleOf(ARG to obj)
}

fun Fragment.getArg(argName: String): String? {
    return arguments?.getString(argName)
}