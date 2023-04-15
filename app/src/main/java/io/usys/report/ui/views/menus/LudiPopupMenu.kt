package io.usys.report.ui.views.menus

import android.view.*
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import io.usys.report.R

class LudiPopupMenu(private val fragment: Fragment, val layout:Int, val action: (View, PopupWindow) -> Unit) : MenuProvider {
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
        val popupView = LayoutInflater.from(fragment.requireContext()).inflate(layout, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // Load animations
        val unfoldAnimation = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.unfold)
        val foldAnimation = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.fold)

        action(popupView, popupWindow)
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
