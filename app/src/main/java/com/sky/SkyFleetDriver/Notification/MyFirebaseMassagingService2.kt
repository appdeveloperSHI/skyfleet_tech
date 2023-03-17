package com.sky.SkyFleetDriver.Notification

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sky.skyfleettech.R

//import com.skyfleetexpress.App
//import com.skyfleetexpress.R
//import com.skyfleetexpress.view.activity.NotificationActivity

class MyFirebaseMassagingService2 : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        // Create an explicit intent for an Activity in app
        val intent = Intent(this, com.sky.SkyFleetDriver.activity.InfoActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        p0.notification?.let {
            intent.putExtra("title", it.title)
            intent.putExtra("message", it.body)
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (p0.notification != null) {
            val builder = NotificationCompat.Builder(this, "push_notification")
                .setSmallIcon(R.drawable.ic_notifications_white)
                .setContentTitle(p0.notification?.title ?: "")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            NotificationManagerCompat.from(this).apply {
                notify(101, builder.build())
            }
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        (application as com.sky.SkyFleetDriver.MyApplication).setFirebaseAppId(p0)

    }
}