package com.sap.codelab.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.sap.codelab.view.home.Home

const val GEO_CHANNEL_ID = "geo_notifications"
const val GEO_CHANNEL_NAME = "Geo-notifications"
const val GEO_NOTIFICATION_REQUEST_CODE = 0x02

fun createNotificationChannel(context: Context, channelId: String, channelName: String, channelDescription: String) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val channel = NotificationChannel(
        channelId,
        channelName,
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = channelDescription
    }
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

fun showGeoNotification(context: Context, title: String, message: String, id: Int) {
    createNotificationChannel(context, GEO_CHANNEL_ID, GEO_CHANNEL_NAME, GEO_CHANNEL_NAME)
    val pendingIntent = createHomeIntent(context, Home::class.java)

    val notification = NotificationCompat.Builder(context, GEO_CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_dialog_map)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

    NotificationManagerCompat.from(context).notify(id, notification)
}

private fun createHomeIntent(context: Context, clazz: Class<*>): PendingIntent =
    TaskStackBuilder.create(context).run {
        addNextIntentWithParentStack(Intent(context, clazz))
        getPendingIntent(GEO_NOTIFICATION_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)!!
    }
