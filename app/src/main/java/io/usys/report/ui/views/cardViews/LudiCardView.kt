package io.usys.report.ui.views.cardViews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import io.usys.report.R
import io.usys.report.utils.ludi.addLudiScrollSliderListener
import io.usys.report.utils.views.bind
import io.usys.report.utils.views.getDrawableCompat

class LudiCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                             defStyleAttr: Int = 0) : CardView(context, attrs, defStyleAttr) {
    var backgroundImageView: ImageView? = null
    var nestedScrollView: NestedScrollView? = null
    var linearLayoutView: LinearLayout? = null

    override fun onViewAdded(child: View?) {
        bindChildren()
    }

    fun attachLudiScrollSlider(nestedScrollView: NestedScrollView?) {
        nestedScrollView?.addLudiScrollSliderListener(this)
    }

    private fun bindChildren() {
        backgroundImageView = this.rootView?.bind(R.id.ludiCardViewBackgroundImageView)
        nestedScrollView = this.rootView?.bind(R.id.ludiCardViewNestedScrollView)
        linearLayoutView = this.rootView?.bind(R.id.ludiCardViewLinearLayout)
    }

    fun changeBackgroundImage(imageResId: Int) {
        backgroundImageView?.background = this.context.getDrawableCompat(imageResId)
        backgroundImageView?.scaleType = ImageView.ScaleType.CENTER_CROP
    }

}


