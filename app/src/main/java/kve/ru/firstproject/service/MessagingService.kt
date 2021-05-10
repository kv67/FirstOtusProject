package kve.ru.firstproject.service

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kve.ru.firstproject.App
import kve.ru.firstproject.MainActivity

class MessagingService : FirebaseMessagingService() {
    companion object {
        const val TAG = "MessagingService"
    }

    override fun onNewToken(token: String) {
        App.token = token
        Log.d(TAG, "New token = $token")
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        // Check if message contains a data payload.
        if (msg.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${msg.data}")
            val intent = Intent(MainActivity.MESSAGE_EVENT)

            msg.data[FilmNotificationPublisher.FILM_ID]?.let {
                 intent.putExtra(FilmNotificationPublisher.FILM_ID, it)
            }

            msg.data[FilmNotificationPublisher.FILM_TITLE]?.let {
                intent.putExtra(FilmNotificationPublisher.FILM_TITLE, it)
            }
            msg.data[FilmNotificationPublisher.FILM_DSC]?.let {
                intent.putExtra(FilmNotificationPublisher.FILM_DSC, it)
            }
            msg.data[FilmNotificationPublisher.FILM_POSTER]?.let {
                intent.putExtra(FilmNotificationPublisher.FILM_POSTER, it)
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }

        // Check if message contains a notification payload.
        msg.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }
}