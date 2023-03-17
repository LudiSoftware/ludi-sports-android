package io.usys.report.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.usys.report.R

/** startActivity **/
inline fun <reified TO> Activity.launchActivity() {
    startActivity(Intent(this, TO::class.java))
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

fun AppCompatActivity.ludiActionBar(): ActionBar? {
    return this.supportActionBar
}

fun Activity.ludiActionBar(): ActionBar? {
    return (this as AppCompatActivity).supportActionBar
}

fun Activity.hideLudiActionBar() {
    this.ludiActionBar()?.hide()
}

fun Activity.showLudiActionBar() {
    this.ludiActionBar()?.show()
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

fun Fragment.hideLudiActionBar() {
    this.requireActivity().hideLudiActionBar()
}
fun Fragment.showLudiActionBar() {
    this.requireActivity().showLudiActionBar()
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
inline fun <reified T> T.fairPickImageFromGalleryTest(crossinline block: (Uri?) -> Unit) {
    if (!isFragment<T>() && !isFragmentActivity<T>()) return
    if (isFragment<T>()) {
        val pickMedia = (this as? Fragment)?.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                block(uri)
            }
        pickMedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    else if (isFragmentActivity<T>()) {
        val pickMedia = (this as? FragmentActivity)?.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            block(uri)
        }
        pickMedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}
inline fun Fragment.fairGetPickImageFromGalleryIntent(crossinline block: (Uri?) -> Unit): ActivityResultLauncher<PickVisualMediaRequest> {
    return registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        block(uri)
    }
}

inline fun FragmentActivity.fairRequestPermission(permission:String= Manifest.permission.WRITE_EXTERNAL_STORAGE, crossinline block: (Boolean) -> Unit) {
    // Registers a photo picker activity launcher in single-select mode.
    val perms = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        block(isGranted)
    }
    perms.launch(permission)
}

inline fun Fragment.fairRequestPermissions(permissions:Array<String>, crossinline block: (Map<String,Boolean>) -> Unit) {
    // Registers a photo picker activity launcher in single-select mode.
    val perms = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        block(it)
    }
    perms.launch(permissions)
}

