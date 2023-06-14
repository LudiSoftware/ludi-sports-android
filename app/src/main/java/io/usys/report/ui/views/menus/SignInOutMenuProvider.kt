package io.usys.report.ui.views.menus

import android.app.Activity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import io.usys.report.R
import io.usys.report.realm.model.Session
import io.usys.report.ui.login.LudiLoginActivity
import io.usys.report.utils.androidx.launchActivity
import io.usys.report.utils.popupYesNo

class SignInOutMenuProvider(private val activity: Activity, val isSignIn:Boolean=true) :
    MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        if (isSignIn) {
            menuInflater.inflate(R.menu.sign_in_menu, menu)
        } else {
            menuInflater.inflate(R.menu.sign_out_menu, menu)
        }
    }
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menuitem_signin -> {
                activity.launchActivity<LudiLoginActivity>()
                return true
            }
            R.id.menuitem_signout -> {
                activity.popupYesNo("Sign Out.", "Are you sure you want to sign out?") {
                    Session.logoutAndRestartApplication(activity)
                }
                return true
            }else -> {}
        }
        return true
    }
}