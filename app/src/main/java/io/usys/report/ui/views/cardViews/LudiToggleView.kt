package io.usys.report.ui.views.cardViews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.ui.ludi.TO_CREATE_NOTE
import io.usys.report.ui.ludi.TO_FORMATION_BUILDER
import io.usys.report.ui.ludi.TO_ROSTER_BUILDER
import io.usys.report.ui.views.listAdapters.loadInCustomAttributes
import io.usys.report.utils.*

class LudiToggleView(context: Context) : MotionLayout(context) {
    constructor(context: Context, attrs: AttributeSet) : this(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context)

    var txtHeaderTitle: TextView? = null
    var switchToggleView: SwitchCompat? = null
    var motionLayout: MotionLayout? = null

    var navController: NavController? = null
    var fragmentContainer: View? = null

    override fun onViewAdded(child: View?) {
        bindChildren()
    }

    private fun bindChildren() {
        txtHeaderTitle = this.rootView.bind(R.id.ludiToggleViewTxtTitle)
//        motionLayout = this.rootView.bind(R.id.ludiToggleViewMotionLayout)
        switchToggleView = this.rootView.bind(R.id.ludiToggleViewSwitch)
        fragmentContainer = this.rootView.bind(R.id.ludiToggleViewFragmentContainer)
        navController = fragmentContainer?.findNavController()
        navController?.navigate(TO_ROSTER_BUILDER, bundleOf("teamId" to "b17bcb69-0fd9-4df1-b61f-8e294f26a87e"))

        switchToggleView?.setOnClickListener {
            this.transitionToEnd()
        }
    }

    fun setTitle(title:String) {
        txtHeaderTitle?.text = title
    }

}