package io.usys.report.coachUser.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.usys.report.R

/**
 * Created by ChazzCoin : December 2019.
 */

class LocManageFragment : Fragment() {

    val _EDIT = 0
    val _DISPLAY = 1
    var MODE = _DISPLAY
    lateinit var rootView : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        rootView = inflater.inflate(R.layout.fragment_locations, container, false)
        //Recycler View

        return rootView
    }

}