package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmObject
import io.usys.report.model.*
import io.usys.report.databinding.FragmentServiceDetailsBinding
import io.usys.report.utils.cast

/**
 * Created by ChazzCoin : October 2022.
 */

class ServiceDetailsFragment : YsrMiddleFragment() {

    private var _binding: FragmentServiceDetailsBinding? = null
    private val binding get() = _binding!!
    private var currentService: Service? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentServiceDetailsBinding.inflate(inflater, container, false)
        rootView = binding.root
        currentService = realmObjectArg?.cast<Service>()
        setupOnClickListeners()
        setupDisplay()
        return rootView
    }

    private fun setupDisplay() {

    }

    override fun setupOnClickListeners() {

    }

}
