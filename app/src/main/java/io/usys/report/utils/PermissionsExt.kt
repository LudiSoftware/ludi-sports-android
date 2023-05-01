package io.usys.report.utils

import android.Manifest
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

val ludiSystemPermissionList = mutableListOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.VIBRATE,
    Manifest.permission.SYSTEM_ALERT_WINDOW
)

inline fun <reified T> T.fairPickImageFromGalleryTest(crossinline block: (Uri?) -> Unit) {
    if (!isFragment<T>() && !isFragmentActivity<T>()) return
    if (isFragment<T>()) {
        val pickMedia = (this as? Fragment)?.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            block(uri)
        }
        pickMedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    else if (isFragmentActivity<T>()) {
        val pickMedia = (this as? FragmentActivity)?.registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()) { uri ->
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


inline fun AppCompatActivity.fairGetPickImageFromGalleryIntent(crossinline block: (Uri?) -> Unit): ActivityResultLauncher<PickVisualMediaRequest> {
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
    val perms = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        block(it)
    }
    perms.launch(permissions)
}
