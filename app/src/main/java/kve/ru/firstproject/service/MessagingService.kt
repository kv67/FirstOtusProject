package kve.ru.firstproject.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {
    companion object {
        const val TAG = "MessagingService"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New token = $token")
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        Log.d(TAG, "From: ${msg.from}")

        // Check if message contains a data payload.
        if (msg.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${msg.data}")
        }

        // Check if message contains a notification payload.
        msg.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }
}