package io.usys.report.utils.androidx

import android.Manifest
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
inline fun Fragment.fairGetCaptureImageFromCameraIntent(photoUri: Uri, crossinline block: (Uri?) -> Unit): ActivityResultLauncher<Uri> {
     // Generate the Uri where the photo will be stored
    return registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            block(photoUri)
        } else {
            block(null)
        }
    }
}


fun Fragment.generatePhotoUri(): Uri {
    val filename = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
    }
    return requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
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
