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
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FirePaths
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireUploadToStorage
import io.usys.report.model.Sport
import io.usys.report.model.User
import io.usys.report.model.getUserId
import io.usys.report.model.userOrLogout
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
        val ARG = "realmObj"
    }

    lateinit var rootView : View
    lateinit var storage: FirebaseStorage
    var itemOnClick: ((View, RealmObject) -> Unit)? = null
    var pickImageIntent: ActivityResultLauncher<PickVisualMediaRequest>? = null

    var user: User? = null
    val main = CoroutineScope(Dispatchers.Main + SupervisorJob())

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
            itUri.fireUploadToStorage(requireContext(), FirePaths.PROFILE_IMAGE_PATH_BY_ID(FireTypes.USERS, getUserId() ?: return@fairGetPickImageFromGalleryIntent))
        }
        setupMenu()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userOrLogout(requireActivity()) { user = it }
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