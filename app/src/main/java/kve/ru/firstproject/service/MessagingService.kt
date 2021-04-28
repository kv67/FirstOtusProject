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
        Log.d(TAG, "From: ${msg.from}")

        // Check if message contains a data payload.
        if (msg.data.isNotEmpty()) {
            Log.d(MainActivity.TAG, "Message data payload: ${msg.data}")
            Log.d(MainActivity.TAG, "filmName = ${msg.data["film_title"]}")
            Log.d(MainActivity.TAG, "filmId = ${msg.data["film_dsc"]}")
            val intent = Intent(MainActivity.MESSAGE_EVENT)
            msg.data["film_title"]?.let {
                intent.putExtra("film_title", it)
            }
            msg.data["film_dsc"]?.let {
                intent.putExtra("film_dsc", it)
            }
            msg.data["film_poster"]?.let {
                intent.putExtra("film_poster", it)
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            // MainActivity.showExtraFilmData(App.instance.applicationContext, )
        }

        // Check if message contains a notification payload.
        msg.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }
}