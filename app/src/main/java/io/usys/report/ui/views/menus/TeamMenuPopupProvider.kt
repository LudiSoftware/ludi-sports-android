package io.usys.report.ui.views.menus

import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import io.usys.report.R
import io.usys.report.ui.views.navController.TO_FORMATION_BUILDER
import io.usys.report.ui.views.navController.TO_ROSTER_BUILDER
import io.usys.report.ui.views.navController.toFragmentWithIds

class TeamMenuPopupProvider(private val fragment: Fragment, private val teamId: String) :
    MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.top_team_menu_dropdown, menu)
    }
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menuitem_options -> {
                showCustomPopup(fragment.requireActivity().findViewById(R.id.menuitem_options))
                return true
            }
            else -> {
            }
        }
        return false
    }

    private fun showCustomPopup(anchorView: View) {
        val popupView = LayoutInflater.from(fragment.requireContext()).inflate(R.layout.team_menu_popup, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Load animations
        val unfoldAnimation = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.unfold)
        val foldAnimation = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.fold)

        // Set up click listeners for the custom menu items
        popupView.findViewById<LinearLayout>(R.id.option_formation).setOnClickListener {
            fragment.toFragmentWithIds(TO_FORMATION_BUILDER, teamId)
            popupWindow.dismiss()
        }

        popupView.findViewById<LinearLayout>(R.id.option_roster).setOnClickListener {
            fragment.toFragmentWithIds(TO_ROSTER_BUILDER, teamId)
            popupWindow.dismiss()
        }

        // If you want to dismiss the popup when clicking outside of it
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        // Set the unfold animation when showing the popup
        popupWindow.contentView.startAnimation(unfoldAnimation)

        // Set the fold animation when dismissing the popup
        popupWindow.setOnDismissListener {
            popupView.startAnimation(foldAnimation)
        }

        // Show the PopupWindow below the anchor view (menu item)
        popupWindow.showAsDropDown(anchorView)
    }
}
