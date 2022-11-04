package io.usys.report.ui.ysrviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import io.usys.report.R
import io.usys.report.utils.*

class YsrExpandableCard(context: Context) : CardView(context) {

    constructor(context: Context, attrs: AttributeSet) : this(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context)

    private var bottomLayout: ConstraintLayout? = null
    private var btnExpandMore: Button? = null
    var bottomLayoutIsVisible: Boolean = false

    override fun onViewAdded(child: View?) {
        bindChildren()
        btnExpandMore?.setOnClickListener {
            toggleExpandCard()
        }
    }

    private fun bindChildren() {
        bottomLayout = this.rootView.bind(R.id.bottomExpandLayout)
        btnExpandMore = this.rootView.bind(R.id.btnExpanderMore)
    }

    private fun toggleExpandCard() {
        if (bottomLayoutIsVisible) {
            collapseCard()
        } else {
            expandCard()
        }
    }

    fun expandCard() {
        btnExpandMore?.text = "less"
        bottomLayoutIsVisible = true
        bottomLayout?.makeVisible()
    }
    fun collapseCard() {
        btnExpandMore?.text = "more"
        bottomLayoutIsVisible = false
        bottomLayout?.makeGone()
    }
}