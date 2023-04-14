package io.usys.report.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.usys.report.R
import io.usys.report.utils.*

/**
 * This is the CustomView Model!
 *      DO NOT USE THIS!
 */
@Deprecated("This is an Example!")
class CopyThisForCustomView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                                      defStyleAttr: Int = 0) : CardView(context, attrs, defStyleAttr) {
    var exampleButton: Button? = null
    var exampleRecyclerView: RecyclerView? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.ludi_custom_attributes_list, this.rootView as ViewGroup)
    }

    // add any custom methods or properties here
    override fun onViewAdded(child: View?) {
        bindChildren()
        setupOnClickListeners()
    }

    private fun bindChildren() {
        exampleRecyclerView = this.rootView?.bind(R.id.ysrRecyclerAddAttribute)
        exampleButton = this.rootView?.bind(R.id.btnAddAttributeCancel)
    }

    private fun setupOnClickListeners() {
        exampleButton?.setOnClickListener {
            log("Cancel button clicked")
        }
    }

}


