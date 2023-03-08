package io.usys.report.ui.ludi.player

import android.graphics.Color
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
import io.usys.report.realm.toHashMap
import io.usys.report.ui.fragments.LudiStringIdsFragment
import io.usys.report.ui.fragments.goBack
import io.usys.report.ui.views.addAttribute
import io.usys.report.utils.log
import io.usys.report.utils.safe
import io.usys.report.utils.views.animateOnClickListener
import io.usys.report.utils.views.loadUriIntoImgView

class PlayerProfileFragment : LudiStringIdsFragment() {

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
        // Includes
        val playerProfile = _binding?.includePlayerProfileHeader
        val playerImage = playerProfile?.cardUserHeaderBasicImgProfile
        playerImage?.animateOnClickListener {
            log("Upload Image")
        }
        // Hide unused views
        val player = realmInstance?.findByField<PlayerRef>("playerId", playerId!!)
        player?.let {
            _binding?.includeYsrListViewNotes?.loadInPlayerRef(it)
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
