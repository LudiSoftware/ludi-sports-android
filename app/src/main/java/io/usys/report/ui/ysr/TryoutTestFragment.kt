package io.usys.report.ui.ysr

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import io.usys.report.R
import io.usys.report.databinding.FragmentHomeTryoutsBinding
import io.usys.report.utils.views.getDrawable
import io.usys.report.utils.views.onMoveListener

/**
 * Created by ChazzCoin : October 2022.
 */

class TryoutTestFragment : Fragment() {

    lateinit var rootView: View
    private var _binding: FragmentHomeTryoutsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeTryoutsBinding.inflate(inflater, container, false)
        rootView = binding.root
        val imgv = ImageView(context)
        imgv.setImageDrawable(getDrawable(context, R.drawable.usysr_logo))

        activity?.window?.let {
            _binding?.root?.addView(imgv)
            imgv.onMoveListener(it)
            _binding?.testDragImageView?.onMoveListener(it)
            _binding?.testDragImageView2?.onMoveListener(it)
            _binding?.testDragImageView3?.onMoveListener(it)
            _binding?.testDragImageView4?.onMoveListener(it)
            _binding?.testDragImageView5?.onMoveListener(it)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}



