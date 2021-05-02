package kve.ru.firstproject.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import kve.ru.firstproject.R
import kve.ru.firstproject.fragments.FilmDetailFragment
import kve.ru.firstproject.repositories.FilmRepository
import java.text.SimpleDateFormat
import java.util.*


class FilmNotificationPublisher : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = "NOTIFICATION_ID"
        const val NOTIFICATION = "NOTIFICATION"
        const val NOTIFICATION_CHANNEL_ID = "Channel_id_001"
        const val FILM_TITLE = "film_title"
        const val FILM_DSC = "film_dsc"
        const val FILM_POSTER = "film_poster"
        private const val NOTIFICATION_CHANNEL_NAME = "Channel_name_001"

        fun sendNotification(
            context: Context,
            filmId: Int,
            filmName: String?,
            filmDsc: String?,
            time: Long
        ) {
            val launchIntent =
                context.packageManager.getLaunchIntentForPackage("kve.ru.firstproject")
            launchIntent?.putExtra(FilmDetailFragment.EXTRA_FILM_ID, filmId)
            launchIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val contentIntent =
                PendingIntent.getActivity(
                    context,
                    filmId,
                    launchIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )

            val mBuilder =
                NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
                    setSmallIcon(R.drawable.movie)
                    setAutoCancel(true)
                    filmName?.let { setContentTitle(filmName) }
                    filmDsc?.let { setContentText(filmDsc) }
                    val alarmSound =
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    setSound(alarmSound)
                    setContentIntent(contentIntent)
                }

            val notificationIntent = Intent(context, FilmNotificationPublisher::class.java)
            notificationIntent.putExtra(NOTIFICATION_ID, filmId)
            notificationIntent.putExtra(NOTIFICATION, mBuilder.build())
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                filmId,
                notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            val offset =
                SystemClock.elapsedRealtime() + (time - Calendar.getInstance().timeInMillis)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, offset] = pendingIntent
        }
    }

    private val repository: FilmRepository = FilmRepository()

    override fun onReceive(context: Context?, intent: Intent?) {
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
                    val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
                    repository.processNotification(filmId, date) {
                        notificationManager.notify(filmId, notification)
                    }
                }
            }
        }
    }
}