package io.usys.report.ui.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
import io.usys.report.ui.fragments.YsrFragment.Companion.ARG
import io.usys.report.ui.fragments.YsrFragment.Companion.ORG_ID
import io.usys.report.ui.fragments.YsrFragment.Companion.PLAYER_ID
import io.usys.report.ui.fragments.YsrFragment.Companion.ROSTER_ID
import io.usys.report.ui.fragments.YsrFragment.Companion.TEAM_ID
import io.usys.report.ui.fragments.YsrFragment.Companion.TYPE
import io.usys.report.ui.ludi.TO_FORMATION_BUILDER
import io.usys.report.ui.ludi.TO_PLAYER_PROFILE
import io.usys.report.ui.ludi.TO_ROSTER_BUILDER
import io.usys.report.ui.ludi.TO_TEAM_PROFILE
import io.usys.report.utils.*
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
            itUri?.uploadImageToFirebaseStorage(userId!!) {
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

fun Fragment.toFragmentWithIds(fragId: Int, teamId:String?=null, playerId:String?=null, orgId:String?=null, type:String?=null) {
    this.findNavController().navigate(fragId, bundleStringIds(teamId, playerId, orgId, type))
}

fun Fragment.toViewRosterFragment(fragId: Int, teamId:String?=null, rosterId:String, type:String?=null) {
    this.findNavController().navigate(fragId, bundleRosterIds(teamId, rosterId, type))
}

fun Fragment.toTeamProfileVG(teamId: String) {
    toFragmentWithRealmObject(TO_TEAM_PROFILE, bundleStringId(teamId))
}
fun Fragment.toPlayerProfile(playerId:String, teamId:String?=null) {
    toFragmentWithIds(TO_PLAYER_PROFILE, teamId = teamId, playerId = playerId)
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

fun Fragment.unbundleRealmObject(): RealmObject? {
    return arguments?.get(ARG) as? RealmObject
}

fun Fragment.unbundleStringId(): String? {
    return arguments?.getString(ARG)
}

fun Fragment.unbundleTeamId(): String? {
    return arguments?.getString(TEAM_ID)
}
fun Fragment.unbundleType(): String? {
    return arguments?.getString(TYPE)
}
fun Fragment.unbundlePlayerId(): String? {
    return arguments?.getString(PLAYER_ID)
}

fun Fragment.unbundleOrgId(): String? {
    return arguments?.getString(ORG_ID)
}

fun bundleRealmObject(obj: RealmObject): Bundle {
    return bundleOf(ARG to obj)
}

fun bundleStringId(obj: String): Bundle {
    return bundleOf(ARG to obj)
}

fun bundleStringIds(teamId: String?=null, playerId: String?=null, orgId: String?=null, type: String?=null): Bundle {
    //teamId, playerId, orgId
    return bundleOf(TEAM_ID to teamId, PLAYER_ID to playerId, ORG_ID to orgId, TYPE to type)
}

fun bundleRosterIds(teamId: String?=null, rosterId: String?=null, type: String?=null): Bundle {
    //teamId, playerId, orgId
    return bundleOf(TEAM_ID to teamId, ROSTER_ID to rosterId, TYPE to type)
}