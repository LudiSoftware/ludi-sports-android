package io.usys.report.ui.ludi

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.usys.report.R
import io.usys.report.firebase.fireludi.fireGetAndLoadSportsIntoSessionAsync
import io.usys.report.providers.UserProvider
import io.usys.report.providers.notifications.createNotificationChannel
import io.usys.report.ui.views.appBar.createBasicAppBarNavigation
import io.usys.report.ui.views.navController.ludiNavController
import io.usys.report.ui.views.statusBar.ludiStatusBarColorWhite


/**
 * Created by ChazzCoin : October 2022.
 */

class MasterUserActivity : AppCompatActivity() {

    var userProvider: UserProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ludiStatusBarColorWhite()
        setContentView(R.layout.activity_main)
        createNotificationChannel(this)

        // -> Base Loading of Data for the user.
        fireGetAndLoadSportsIntoSessionAsync()
        userProvider = UserProvider()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = ludiNavController()
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = createBasicAppBarNavigation()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

}

inline fun FragmentActivity.onBackPressed(crossinline onBackPressed: () -> Unit) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressed()
        }
    })
}

inline fun Fragment.onBackPressed(crossinline onBackPressed: () -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressed()
        }
    })
}

inline fun Fragment.onEnterKeyPressed(editText: EditText, crossinline onEnterPressed: () -> Unit) {
    editText.setOnEditorActionListener { _: TextView, actionId: Int, event: KeyEvent? ->
        if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
            onEnterPressed()
            true
        } else {
            false
        }
    }
}







