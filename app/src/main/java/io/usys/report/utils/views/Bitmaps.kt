package io.usys.report.utils.views

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

//fun Bitmap.saveBitmapToFile(context: Context, filename: String, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100) {
//    val file = File(context.cacheDir, filename)
//    val stream = FileOutputStream(file)
//    this.compress(format, quality, stream)
//    stream.flush()
//    stream.close()
//}

fun Bitmap.toBase64(): String {
    val outputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun String.fromBase64ToBitmap(): Bitmap {
    val decodedString = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}
