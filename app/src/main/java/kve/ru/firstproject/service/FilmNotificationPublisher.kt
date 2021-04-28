package kve.ru.firstproject.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build


class FilmNotificationPublisher : BroadcastReceiver() {

    companion object {
        const val TAG = "FilmNotificationPub"
        const val NOTIFICATION_ID = "NOTIFICATION_ID"
        const val NOTIFICATION = "NOTIFICATION"
        const val NOTIFICATION_CHANNEL_ID = "Channel_id_001"
        private const val NOTIFICATION_CHANNEL_NAME = "Channel_name_001"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
//        Log.d(TAG, "OnReceive: main is active - ${App.isMainActivityActive}")
        intent?.let {
            val filmId = it.getIntExtra(NOTIFICATION_ID, 0)
            val notification: Notification? = it.getParcelableExtra(NOTIFICATION)

            notification?.let {
                if (filmId > 0) {
                    val notificationManager =
                        context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val importance = NotificationManager.IMPORTANCE_DEFAULT
                        val notificationChannel = NotificationChannel(
                            NOTIFICATION_CHANNEL_ID,
                            NOTIFICATION_CHANNEL_NAME, importance
                        )
                        notificationChannel.enableVibration(true)
                        notificationChannel.vibrationPattern =
                            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                        notificationManager.createNotificationChannel(notificationChannel)
                    }

                    notificationManager.notify(filmId, notification)
                }
            }
        }
    }
}