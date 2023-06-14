package io.usys.report.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import io.realm.Realm
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.uploadImageToFirebaseStorage
import io.usys.report.realm.model.users.User
import io.usys.report.realm.model.users.safeUser
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.realm.realm
import io.usys.report.ui.views.navController.unbundleRealmObject
import io.usys.report.utils.*
import io.usys.report.utils.androidx.fairGetPickImageFromGalleryIntent
import io.usys.report.utils.androidx.fairRequestPermissions
import io.usys.report.utils.androidx.ludiSystemPermissionList
import org.jetbrains.anko.support.v4.toast

/**
 * Created by ChazzCoin : October 2022.
 * * Make sure to add any new fragments to Navigation XML! * *
 */

abstract class YsrFragment : Fragment() {

    companion object {
        const val ARG = "realmObj"
        const val TYPE = "type"
        const val TEAM_ID = "teamId"
        const val PLAYER_ID = "playerId"
        const val ORG_ID = "orgId"
        const val ROSTER_ID = "rosterId"

    }

    lateinit var rootView : View
    lateinit var storage: FirebaseStorage
    var itemOnClick: ((View, RealmObject) -> Unit)? = null
    var pickImageIntent: ActivityResultLauncher<PickVisualMediaRequest>? = null

    var user: User? = null
    var userId: String? = null

    var realmInstance: Realm? = null
    var realmObjectArg: RealmObject? = null

    abstract fun setupOnClickListeners()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        realmInstance?.safeUserId {
            userId = it
        }

        fairRequestPermissions(permissions = ludiSystemPermissionList.toTypedArray()) { mapOfResults ->
            log(mapOfResults.toString())
        }

        //Create Initial Intent for Uploading Image.
        pickImageIntent = fairGetPickImageFromGalleryIntent { itUri ->
            log(itUri)
            itUri?.uploadImageToFirebaseStorage(userId!!, requireContext()) {
                toast("Image Uploaded Successfully!")
            }
        }

        // Testing Uploading Image to Firebase Storage
//        setupUploadPhotoMenu()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        realmInstance = realm()
        realmInstance?.safeUser {
            user = it
            userId = it.id
        }
        storage = Firebase.storage
        realmObjectArg = unbundleRealmObject()
    }


    private fun setupUploadPhotoMenu() {
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

/** Ludi Status Bar **/
fun Fragment.ludiStatusBarColor(@ColorRes color: Int= R.color.ysrWindowBackground) {
    (requireActivity() as AppCompatActivity).let {
        it.window.statusBarColor = ContextCompat.getColor(it, color)
    }
}
fun Fragment.ludiStatusBarTeamTryoutMode() {
    (requireActivity() as AppCompatActivity).let {
        it.window.statusBarColor = ContextCompat.getColor(it, R.color.ysrFadedRed)
    }
}
fun Fragment.ludiStatusBarTeamInSeasonMode() {
    (requireActivity() as AppCompatActivity).let {
        it.window.statusBarColor = ContextCompat.getColor(it, R.color.ysrFadedBlue)
    }
}
