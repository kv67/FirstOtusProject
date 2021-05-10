package kve.ru.firstproject.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import kve.ru.firstproject.R
import kve.ru.firstproject.fragments.FilmDetailFragment
import kve.ru.firstproject.repositories.FilmRepository


class FilmNotificationPublisher : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = "NOTIFICATION_ID"
        const val NOTIFICATION = "NOTIFICATION"
        const val FILM_ID = "film_id"
        const val FILM_TITLE = "film_title"
        const val FILM_DSC = "film_dsc"
        const val FILM_POSTER = "film_poster"
        private const val NOTIFICATION_CHANNEL_NAME = "Channel_name_001"
        private const val NOTIFICATION_CHANNEL_ID = "Channel_id_001"

        private val repository = FilmRepository()

        private fun getChannel(): NotificationChannel? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationChannel.enableVibration(true)
                notificationChannel.vibrationPattern =
                    longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                notificationChannel
            } else {
                null
            }
        }

        private fun sendNotification(
            context: Context,
            filmId: Int,
            filmName: String?,
            filmDsc: String?,
            time: Long,
            sendNow: Boolean
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

            if (sendNow) {
                if (filmId > 0) {
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getChannel()?.let { channel ->
                            notificationManager.createNotificationChannel(channel)
                        }
                    }
                    notificationManager.notify(filmId, mBuilder.build())
                }
            } else {
                val notificationIntent = Intent(context, FilmNotificationPublisher::class.java)
                notificationIntent.putExtra(NOTIFICATION_ID, filmId)
                notificationIntent.putExtra(NOTIFICATION, mBuilder.build())
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    filmId,
                    notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    alarmManager.setAlarmClock(
                        AlarmManager.AlarmClockInfo(time, pendingIntent),
                        pendingIntent
                    )
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        time,
                        pendingIntent
                    )
                }
            }
        }

        fun cancelNotification(
            context: Context,
            filmId: Int
        ) {
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                filmId,
                Intent(context, FilmNotificationPublisher::class.java),
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }

        fun sendFilmNotification(
            context: Context,
            filmId: Int, time: Long,
            sendNow: Boolean
        ) {
            repository.getFilmForNotification(filmId) {film ->
                sendNotification(context, film.id, film.name, film.dsc, time, sendNow)
            }
        }
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val filmId = it.getIntExtra(NOTIFICATION_ID, 0)
            val notification: Notification? = it.getParcelableExtra(NOTIFICATION)

            notification?.let {
                if (filmId > 0) {
                    val notificationManager =
                        context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getChannel()?.let { channel ->
                            notificationManager.createNotificationChannel(channel)
                        }
                    }
                    repository.processNotification(filmId) {
                        notificationManager.notify(filmId, notification)
                    }
                }
            }
        }
    }
}