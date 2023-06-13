package io.usys.report.utils.androidx

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.usys.report.R
import io.usys.report.utils.views.getColorCompat

/** startActivity **/
inline fun <reified TO> Activity.launchActivity() {
    startActivity(Intent(this, TO::class.java))
}



//onBackPressed
inline fun FragmentActivity.onBackButtonPressed(crossinline block: () -> Unit) {
    this.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // Your custom back button behavior goes here
            isEnabled = false // This is important to let the back press event propagate to the default behavior after your custom behavior is executed.
            block()
        }
    })
}

/** Register for Activity Result **/
inline fun <reified CONTRACT> FragmentActivity.fairRegisterActivityResult(intent: Intent, crossinline block: (CONTRACT) -> Unit) {
    this.registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        (res as? CONTRACT)?.let { block(it) }
    }.launch(intent)
}

/** Pick Image From Gallery Intent **/
inline fun Fragment.fairPickImageFromGallery(crossinline block: (Uri?) -> Unit) {
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        block(uri)
    }
    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType("image/gif")))
}

fun Activity.isKeyboardVisible(): Boolean {
    val rootView = this.window.decorView.rootView
    val visibleBounds = Rect()
    rootView.getWindowVisibleDisplayFrame(visibleBounds)

    val heightDiff = rootView.height - visibleBounds.height()
    val marginOfError = resources.getDimensionPixelSize(R.dimen.keyboard_visibility_margin)
    return heightDiff > marginOfError
}

inline fun <reified T> isFragmentOrActivity() = when (T::class) {
    Fragment::class -> true
    Activity::class -> true
    else -> false
}

inline fun <reified T> isActivity(): Boolean {
    val tClass = T::class.javaClass.superclass
    if (tClass.isInstance(Activity::class)) return true
    return false
}

inline fun <reified T> isFragment(): Boolean {
    val tClass = T::class.javaClass.superclass
    if (tClass.isInstance(Fragment::class)) return true
    return false
}

inline fun <reified T> isFragmentActivity(): Boolean {
    val tClass = T::class.javaClass.superclass
    if (tClass.isInstance(FragmentActivity::class)) return true
    return false
}



fun Activity.ludiNavView(): View? {
    return this.findViewById<BottomNavigationView>(R.id.nav_view)
}

fun Activity.hideLudiNavView() {
    this.ludiNavView()?.visibility = View.GONE
}

fun Activity.showLudiNavView() {
    this.ludiNavView()?.visibility = View.VISIBLE
}

fun Fragment.hideLudiNavView() {
    this.requireActivity().hideLudiNavView()
}
fun Fragment.showLudiNavView() {
    this.requireActivity().showLudiNavView()
}

fun Activity.changeStatusBarColor() {
    this.window.statusBarColor = this.getColorCompat(android.R.color.black)
}

