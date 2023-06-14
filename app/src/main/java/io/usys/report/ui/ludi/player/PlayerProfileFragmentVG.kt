package io.usys.report.ui.ludi.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.databinding.PlayerProfileVgFragmentBinding
import io.usys.report.firebase.firebaseDatabase
import io.usys.report.firebase.realmListToDataList
import io.usys.report.firebase.uploadImageToFirebaseStorage
import io.usys.report.providers.fireRosterUpdatePlayers
import io.usys.report.realm.findByField
import io.usys.report.realm.findRosterById
import io.usys.report.realm.model.*
import io.usys.report.realm.safeWrite
import io.usys.report.ui.fragments.LudiStringIdsFragment
import io.usys.report.ui.ludi.onBackPressed
import io.usys.report.ui.views.hideLudiActionBar
import io.usys.report.ui.views.navController.goBack
import io.usys.report.ui.views.viewGroup.LudiViewGroup
import io.usys.report.ui.views.viewGroup.ludiPlayerProfileFragments
import io.usys.report.utils.androidx.fairGetCaptureImageFromCameraIntent
import io.usys.report.utils.androidx.generatePhotoUri
import io.usys.report.utils.log
import io.usys.report.utils.views.animateOnClickListener
import io.usys.report.utils.views.loadUriIntoImgView
import org.jetbrains.anko.support.v4.toast


class PlayerProfileFragmentVG : LudiStringIdsFragment() {

    companion object {
        const val DISPLAY_MODE = "display_mode"
        const val EDIT_MODE = "edit_mode"
    }

    var _MODE = DISPLAY_MODE
    var cameraImageIntent: ActivityResultLauncher<Uri>? = null
    var cameraUri: Uri? = null
    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: PlayerProfileVgFragmentBinding? = null
    private val binding get() = _binding!!

    private var playerNotes: RealmList<Note>? = RealmList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        if (container == null) {
            _binding = PlayerProfileVgFragmentBinding.inflate(inflater, teamContainer, true)
        } else {
            _binding = PlayerProfileVgFragmentBinding.inflate(inflater, container, false)
        }

        rootView = binding.root

        hideLudiActionBar()
        requireActivity().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        onBackPressed {
            this.goBack()
        }

        //Basic Setup
        setupDisplay()
        return rootView
    }

    private fun setupDisplay() {
        // Buttons

        val ll = _binding?.playerProfileLinearLayout
        val lvg = ll?.let {
            LudiViewGroup(requireParentFragment(), it, teamId, playerId = playerId)
        }
        lvg?.setupLudiTabs(ludiPlayerProfileFragments())

        // Includes
        val playerProfile = _binding?.includePlayerProfileHeader
        val playerImage = playerProfile?.cardUserHeaderBasicImgProfile
        playerImage?.animateOnClickListener {
            log("Upload Image")
            cameraImageIntent?.launch(cameraUri)
        }

        cameraUri = generatePhotoUri()
        cameraImageIntent = fairGetCaptureImageFromCameraIntent(cameraUri!!) { uri ->
            // Do something with the uri
            log(uri)
            var durl = ""
            uri?.uploadImageToFirebaseStorage(playerId!!, requireContext()) { downloadUri ->
                durl = downloadUri.path.toString()
                toast("Image Uploaded Successfully!")
                //todo: change the players image


                realmInstance?.safeWrite {
                    val roster = realmInstance?.findRosterById(rosterId)
                    for (player in roster!!.players!!) {
                        if (player.id == playerId) {
                            player.imgUrl = downloadUri.toString()
                        }
                    }
                    rosterId?.let {
                        realmInstance?.fireRosterUpdatePlayers(it)
                        val data = realmListToDataList(roster.players!!)
                        firebaseDatabase { itDB ->
                            itDB.child("rosters")
                                .child(it)
                                .child("players")
                                .setValue(data)
                        }
                    }
                }
            }
        }

        // Hide unused views
        val player = realmInstance?.findByField<PlayerRef>("playerId", playerId!!)
        player?.let {
            it.imgUrl?.let { imgUrl ->
                playerImage?.loadUriIntoImgView(imgUrl)
            }
        }
    }

//    fun launchPickImageIntent(){
//        pickImageIntent?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//    }

}

fun rotateBitmap(context: Context, bitmap: Bitmap, photoUri: Uri): Bitmap? {
    val inputStream = context.contentResolver.openInputStream(photoUri)
    val ei: ExifInterface
    ei = if (Build.VERSION.SDK_INT > 23) ExifInterface(inputStream!!)
    else ExifInterface(photoUri.path!!)

    val orientation: Int = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL)

    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270F)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipImage(bitmap, true, false)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipImage(bitmap, false, true)
        else -> bitmap
    }
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
        matrix, true)
}

fun flipImage(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
    val matrix = Matrix()
    matrix.preScale((if (horizontal) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private fun isInsideView(view: View, x: Float, y: Float): Boolean {
    val location = IntArray(2)
    view.getLocationOnScreen(location)
    val left = location[0]
    val top = location[1]
    val right = left + view.width
    val bottom = top + view.height
    return x >= left && x <= right && y >= top && y <= bottom
}
