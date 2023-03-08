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
import io.usys.report.realm.model.CustomAttribute
import io.usys.report.realm.model.Note
import io.usys.report.realm.model.PlayerRef
import io.usys.report.ui.fragments.LudiStringIdsFragment
import io.usys.report.ui.fragments.goBack
import io.usys.report.utils.log
import io.usys.report.utils.safe
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
        val playerTxtName = playerProfile?.cardUserHeaderBasicTxtProfileName
        val playerImage = playerProfile?.cardUserHeaderBasicImgProfile
        // Hide unused views
        val player = realmInstance?.findByField<PlayerRef>("playerId", playerId!!)
        player?.let {
            val tca = RealmList<CustomAttribute>()
            tca.addAttribute("name", it.name.safe("No Name"))
            tca.addAttribute("position", it.position.safe("No Position"))
            tca.addAttribute("tryout tag", it.tryoutTag.safe("No Tryout Tag"))
            tca.addAttribute("dob", it.dob.safe("No DOB"))
            tca.addAttribute("dominate foot", it.foot.safe("No Dominate Foot"))
            tca.addAttribute("jersey number", it.number.toString())
            tca.addAttribute("rank", it.rank.toString().safe("No Rank"))
            _binding?.includeYsrListViewNotes?.loadInRealmList(tca)
//            _binding?.includeYsrListViewNotes?.ysrRecyclerAddAttribute?.loadInCustomAttributes(tca)
            playerTxtName?.text = it.name
            it.imgUrl?.let { imgUrl ->
                playerImage?.loadUriIntoImgView(imgUrl)
            }
        }
    }

}

fun RealmList<CustomAttribute>.addAttribute(key:String, value:String) {
    this.add(CustomAttribute().apply {
        add(key, value)
    })
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
