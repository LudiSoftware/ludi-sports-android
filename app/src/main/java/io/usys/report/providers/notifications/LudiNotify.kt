package io.usys.report.providers.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.usys.report.R
import io.usys.report.ui.ludi.MasterUserActivity

private const val CHANNEL_ID = "ludi_channel_id_1"
private const val CHANNEL_NAME = "Example Notification Channel"

private const val NOTIFICATION_CHAT_ID = 1001

fun ludiChatNotify(context: Context?, message: String) {
    context?.let { showNotification(it, NOTIFICATION_CHAT_ID, "Ludi Chat Message", message) }
}

fun createNotificationChannel(context: Context) {
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
        description = "Ludi Notification Channel"
        enableLights(true)
        lightColor = Color.RED
        enableVibration(true)
        setShowBadge(true)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    }
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

@SuppressLint("MissingPermission")
fun showNotification(context: Context, notificationId: Int, title: String, message: String) {
    val intent = Intent(context, MasterUserActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ludi_icon_one) // Replace with your app's notification icon
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.notify(notificationId, notification)
}
