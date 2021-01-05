package com.yacov.countingdays.data.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yacov.countingdays.R
import com.yacov.countingdays.ui.MainActivity

class PushFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // To receive notifications in background
        // Needed to pass data param
        // Reference: https://stackoverflow.com/a/37845174

        val title: String
        val body: String
        if (remoteMessage.notification != null) {
            title = remoteMessage.notification!!.title.toString()
            body = remoteMessage.notification!!.body.toString()
        } else {
            title = remoteMessage.data["title"].toString()
            body = remoteMessage.data["body"].toString()
        }

        showNotification(this, title, body, remoteMessage.data)
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.e("newToken", s)
        getSharedPreferences("firebase", MODE_PRIVATE).edit().putString("token", s).apply()
    }

    fun getToken(context: Context): String {
        return context.getSharedPreferences("firebase", MODE_PRIVATE).getString("token", "") ?: ""
    }

    private fun showNotification(context: Context, title: String, message: String, customData: Map<String, String>? = null, id: Int = 0) {

        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        notificationIntent.putExtra("title", title)
        notificationIntent.putExtra("body", message)

        if (customData != null) {
            notificationIntent.putExtra("data", HashMap(customData))
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val channelId = "1"
        val builder = NotificationCompat.Builder(context, channelId)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.love_draw)
            .setColor(context.resources.getColor(R.color.colorAccent, null))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channelId)
            val notificationChannel = NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(id, builder.build())
    }
}
