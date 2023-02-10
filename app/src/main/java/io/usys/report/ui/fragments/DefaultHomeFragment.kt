package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import io.usys.report.R
import io.usys.report.databinding.DefaultFullHomeBinding

class DefaultHomeFragment : Fragment() {

    private lateinit var binding: DefaultFullHomeBinding
    var headerCard: View? = null
    var topTitle: View? = null
    var topDetails: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = DefaultFullHomeBinding.inflate(inflater, container, false)

        headerCard = binding.includeCardCoach.root
        topTitle = binding.includeItemTitleList.root
        topDetails = binding.includeCardDetails.root
        binding.includeGenericButtonCardOne
        binding.includeGenericButtonCardTwo
        binding.includeGenericButtonCardThree.root.setOnClickListener {

        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: Header
        binding.includeCardCoach.cardCoachTxtName.text = "Coach Name"
        // TODO: Change top title details
        binding.includeItemTitleList.itemTitleListTxtTitle.text = "Title"
        binding.includeCardDetails.cardDetailsTxtDetails.text = "Details Title"
        binding.includeGenericButtonCardOne.cardGenericButtonTxtTitle.text = "Button 1"

    }

    fun setHeader() {
        binding.includeCardCoach.cardCoachTxtName.text = "Coach Name"
        binding.includeCardCoach.cardCoachTxtEmail.text = ""
        binding.includeCardCoach.cardCoachTxtOrg.text = ""

    }

}

class DataBindingExtensions {
    @BindingAdapter("app:srcCompat")
    fun setImageResource(view: ImageView, resource: Int) {
        view.setImageResource(resource)
    }
}