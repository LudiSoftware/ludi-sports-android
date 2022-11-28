package io.usys.report.ui.ysr

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.View.OnTouchListener
import android.widget.LinearLayout.*
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.utils.MDUtil.getWidthAndHeight
import io.usys.report.databinding.FragmentHomeTryoutsBinding


/**
 * Created by ChazzCoin : October 2022.
 */

class TryoutTestFragment : Fragment() {

    lateinit var rootView: View
    private var _binding: FragmentHomeTryoutsBinding? = null
    private val binding get() = _binding!!

//    var itemOnClickSportList: ((View, RealmObject) -> Unit)? = onClickReturnViewRealmObject()
//    var itemOnClickServiceList: ((View, RealmObject) -> Unit)? = onClickReturnViewRealmObject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeTryoutsBinding.inflate(inflater, container, false)
        rootView = binding.root

        activity?.window?.let {
            _binding?.testDragImageView?.ysrOnMoveListener(it)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

@SuppressLint("ClickableViewAccessibility")
fun View?.ysrOnMoveListener(window: Window?) {

    val windowWidthAndHeight = window?.windowManager?.getWidthAndHeight()
    val windowWidth = windowWidthAndHeight?.first ?: 0
    val windowHeight = windowWidthAndHeight?.second ?: 0
    this?.setOnTouchListener { _, event ->
        val layoutParams: LayoutParams = this.layoutParams as LayoutParams
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {}
            MotionEvent.ACTION_MOVE -> {
                var x_cord = event.rawX.toInt()
                var y_cord = event.rawY.toInt()
                if (x_cord > windowWidth) {
                    x_cord = windowWidth
                }
                if (y_cord > windowHeight) {
                    y_cord = windowHeight
                }
                layoutParams.leftMargin = x_cord - 100
                layoutParams.topMargin = y_cord - 200
                this.layoutParams = layoutParams
            }
            else -> {}
        }
        true
    }
}

