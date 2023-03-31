package io.usys.report.ui.ludi.player

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.usys.report.R
import io.usys.report.databinding.DetailsListFragmentBinding
import io.usys.report.realm.findByField
import io.usys.report.realm.model.*
import io.usys.report.ui.fragments.LudiStringIdsFragment

class DetailsListFragment : LudiStringIdsFragment() {
    companion object {
        const val DISPLAY_MODE = "display_mode"
        const val EDIT_MODE = "edit_mode"
    }
    var _MODE = DISPLAY_MODE
    private var _binding: DetailsListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        if (container == null) {
            _binding = DetailsListFragmentBinding.inflate(inflater, teamContainer, false)
        } else {
            _binding = DetailsListFragmentBinding.inflate(inflater, container, false)
        }

        rootView = binding.root
        requireActivity().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //Basic Setup
        setupDisplay()
        return rootView
    }

    private fun setupDisplay() {
        val player = realmInstance?.findByField<PlayerRef>("playerId", playerId)
        player?.let {
            _binding?.includeDetailListFragmentLudiAttributesList?.loadInPlayerRef(it)
        }
    }

}

