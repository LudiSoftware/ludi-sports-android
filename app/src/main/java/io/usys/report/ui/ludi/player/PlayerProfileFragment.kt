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
import io.usys.report.realm.model.Note
import io.usys.report.realm.model.PlayerRef
import io.usys.report.ui.fragments.LudiStringIdsFragment
import io.usys.report.ui.fragments.goBack
import io.usys.report.utils.log
import io.usys.report.utils.views.loadUriIntoImgView

class PlayerProfileFragment : LudiStringIdsFragment() {

    companion object {
        const val TAB = "Notes"
    }

    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: DialogPlayerProfileLayoutBinding? = null
    private val binding get() = _binding!!

    private var playerNotes: RealmList<Note>? = RealmList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        _binding = DialogPlayerProfileLayoutBinding.inflate(inflater, teamContainer, false)
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
        val playerTxtName = playerProfile?.cardUserHeaderBasicTxtProfileName
        val playerTxtRank = _binding?.txtPlayerDialogRank
        val playerImage = playerProfile?.cardUserHeaderBasicImgProfile
        val playerTxtTryoutTag = _binding?.txtPlayerDialogTryOutTag
        val playerTxtPosition = _binding?.txtPlayerDialogPosition
        // date of birth
        val playerTxtDOB = _binding?.txtPlayerDialogDOB
        // age
        val playerTxtAge = _binding?.txtPlayerDialogAge
        // jersey number
        val playerTxtJerseyNumber = _binding?.txtPlayerDialogJerseyNumber
        val includeNotes = _binding?.includeYsrListViewNotes
        val includeNotesRecyclerView = includeNotes?.ysrCreateListRecycler
        val includeNotesTitle = includeNotes?.ysrCreateListTxtTitle
        // Hide unused views
        val player = realmInstance?.findByField<PlayerRef>("playerId", playerId!!)
        player?.let {
            playerTxtName?.text = it.name
            playerTxtRank?.text = "Rank: ${it.rank.toString()}"
            playerTxtPosition?.text = "Position: ${it.position}"
            playerTxtTryoutTag?.text = "Tryout Tag: ${it.tryoutTag}"
            playerTxtDOB?.text = "DOB: ${it.dob}"
            playerTxtAge?.text = "Dominate Foot: ${it.foot}"
            playerTxtJerseyNumber?.text = "Jersey Number: ${it.number}"
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
