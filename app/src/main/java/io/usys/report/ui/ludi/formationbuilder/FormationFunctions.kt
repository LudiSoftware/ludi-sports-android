package io.usys.report.ui.ludi.formationbuilder

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import io.usys.report.R
import io.usys.report.utils.views.getDrawable
import io.usys.report.utils.views.getRelativeLayoutParams
import org.jetbrains.anko.backgroundColor

/** FORMATION LAYOUT: Layout Params **/
fun preparePlayerLayoutParamsForFormation(loadingFromSession: Boolean=false): RelativeLayout.LayoutParams {
    val layoutParams = getRelativeLayoutParams()
    layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT
    layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
    return layoutParams
}

/** FORMATION LAYOUT: Extension Helpers Below **/
fun CardView.setPlayerFormationBackgroundColor(colorName: String?) {
    val color = when (colorName) {
        "red" -> ContextCompat.getColor(this.context, R.color.ysrFadedRed)
        "blue" -> ContextCompat.getColor(this.context, R.color.ysrFadedBlue)
        else -> Color.TRANSPARENT
    }
    this.setCardBackgroundColor(color)
}
fun View.setPlayerTeamBackgroundColor(colorName: String?) {
    val color = when (colorName) {
        "red" -> ContextCompat.getColor(this.context, R.color.ysrFadedRed)
        "blue" -> ContextCompat.getColor(this.context, R.color.ysrFadedBlue)
        else -> Color.TRANSPARENT
    }
    backgroundColor = color
}
fun getColor(): Int {
    return Color.parseColor("#FF0000")
}
fun ImageView.setPlayerTeamBackgroundBanner(colorName: String?) {
    var bg: Drawable? = getDrawable(this.context, R.drawable.player_name_banner_red)
    val color = when (colorName) {
        "red" -> bg = getDrawable(this.context, R.drawable.player_name_banner_red)
        "blue" -> bg = getDrawable(this.context, R.drawable.player_name_banner_blue)
        else -> getDrawable(this.context, R.drawable.player_name_banner_red)
    }
    background = bg
}

fun String.parseColor(): Int {
    return Color.parseColor(this)
}

fun getBackgroundDrawable(context: Context, drawableReference: Int): Drawable? {
    return getDrawable(context, drawableReference)
}