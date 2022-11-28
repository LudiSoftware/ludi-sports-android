package io.usys.report.utils.views

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import io.usys.report.utils.toUri

fun Context.loadUriIntoImgView(uri: Uri, imageView: ImageView) {
    Glide.with(this).load(uri).into(imageView)
}

fun Fragment.loadUriIntoImgView(uri: Uri, imageView: ImageView) {
    Glide.with(this).load(uri).into(imageView)
}

fun Fragment.loadUriIntoImgView(stringUri: String, imageView: ImageView) {
    val uri = stringUri.toUri()
    Glide.with(this).load(uri).into(imageView)
}