package io.usys.report.ui.ludi.player

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.databinding.DialogPlayerProfileLayoutBinding
import io.usys.report.realm.findByField
import io.usys.report.realm.model.*
import io.usys.report.ui.fragments.LudiStringIdsFragment
import io.usys.report.ui.fragments.goBack
import io.usys.report.ui.views.LudiViewGroup
import io.usys.report.ui.views.ludiPlayerProfileFragments
import io.usys.report.utils.hideLudiActionBar
import io.usys.report.utils.log
import io.usys.report.utils.views.animateOnClickListener
import io.usys.report.utils.views.loadUriIntoImgView


class PlayerProfileFragment : LudiStringIdsFragment() {

    companion object {
        const val DISPLAY_MODE = "display_mode"
        const val EDIT_MODE = "edit_mode"
    }

    var _MODE = DISPLAY_MODE

    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: DialogPlayerProfileLayoutBinding? = null
    private val binding get() = _binding!!

    private var playerNotes: RealmList<Note>? = RealmList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        if (container == null) {
            _binding = DialogPlayerProfileLayoutBinding.inflate(inflater, teamContainer, false)
        } else {
            _binding = DialogPlayerProfileLayoutBinding.inflate(inflater, container, false)
        }

        rootView = binding.root

        hideLudiActionBar()
        requireActivity().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //Basic Setup
        setupDisplay()
        return rootView
    }

    private fun setupDisplay() {
        // Buttons
        _binding?.btnPlayerDialogCreateEval?.setOnClickListener {
            log("Create Evaluation")
            this.goBack()
        }
        _binding?.btnPlayerDialogSendOffer?.setOnClickListener {
            log("Send Offer")
        }
        _binding?.btnEditModePlayerProfile?.animateOnClickListener {
            log("Edit Mode")
            if (_MODE == DISPLAY_MODE) {
                _MODE = EDIT_MODE
            } else {
                _MODE = DISPLAY_MODE
            }
        }

        val ll = _binding?.playerProfileLinearLayout
//        val rl = _binding?.reviewRootCard
        val lvg = LudiViewGroup(requireParentFragment(), ll!!, teamId, playerId = playerId)
        lvg.setupLudiTabs(ludiPlayerProfileFragments())

        // Add an OnGlobalLayoutListener to the LinearLayout's view tree observer
//        rl?.viewTreeObserver?.addOnGlobalLayoutListener {
//            // Get the height of the root view
//            val height: Int = requireActivity().window.decorView.rootView.height
//            // Get the height of the visible portion of the root view
//            val rect = Rect()
//            requireActivity().window.decorView.getWindowVisibleDisplayFrame(rect)
//            val visibleHeight: Int = rect.height()
//            // Calculate the size of the keyboard
//            val keyboardHeight = height - visibleHeight
//            // If the keyboard is visible, move the LinearLayout up
//            if (keyboardHeight > 0) {
//                rl.translationY = (-keyboardHeight).toFloat()
//            } else {
//                // If the keyboard is hidden, move the LinearLayout back down
//                rl.translationY = 0F
//            }
//        }

        // Includes
        val playerProfile = _binding?.includePlayerProfileHeader
        val playerImage = playerProfile?.cardUserHeaderBasicImgProfile
        playerImage?.animateOnClickListener {
            log("Upload Image")
        }
        // Hide unused views
        val player = realmInstance?.findByField<PlayerRef>("playerId", playerId!!)
        player?.let {
            it.imgUrl?.let { imgUrl ->
                playerImage?.loadUriIntoImgView(imgUrl)
            }
        }
    }

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
