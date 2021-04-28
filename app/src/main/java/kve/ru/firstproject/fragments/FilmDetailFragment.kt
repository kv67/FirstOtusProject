package kve.ru.firstproject.fragments

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.RingtoneManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kve.ru.firstproject.MainActivity
import kve.ru.firstproject.R
import kve.ru.firstproject.db.Film
import kve.ru.firstproject.model.FilmViewModel
import kve.ru.firstproject.service.FilmNotificationPublisher
import java.util.*


class FilmDetailFragment : Fragment() {

    companion object {
        const val TAG = "FilmDetailFragment"
        const val EXTRA_FILM_ID = "EXTRA_FILM_ID"
    }

    private var currentFilm: Film? = null

    private val imageViewPoster by lazy {
        view?.findViewById<ImageView>(R.id.imageViewPoster)
    }
    private val textViewDsc by lazy {
        view?.findViewById<TextView>(R.id.textViewDsc)
    }
    private val checkBoxLike by lazy {
        view?.findViewById<CheckBox>(R.id.checkBoxLike)
    }
    private val editTextComment by lazy {
        view?.findViewById<EditText>(R.id.editTextComment)
    }
    private val buttonNotify by lazy {
        view?.findViewById<FloatingActionButton>(R.id.fbuNotify)
    }
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[FilmViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_film_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        buttonNotify?.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val dialog = DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Material_Light_Dialog,
                { _: DatePicker, y: Int, m: Int, d: Int ->
//                    val date = "$d.${if (m + 1 < 10) "0" else ""}${m + 1}.$y"
                    // Toast.makeText(requireContext(), date, Toast.LENGTH_SHORT).show()
                    Calendar.getInstance().let { cl ->
                        cl.set(y, m, d, 10, 0, 0)
                        sendNotification(cl.timeInMillis)
                    }

                },
                year, month, day
            )
            cal.add(Calendar.DATE, 1)
            dialog.datePicker.minDate = cal.timeInMillis
            cal.add(Calendar.DATE, 15)
            dialog.datePicker.maxDate = cal.timeInMillis
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            dialog.show()
        }

        viewModel.selectedFilm.observe(viewLifecycleOwner, {
            it?.let {
                currentFilm = it
                requireActivity().title = it.name
                editTextComment?.setText(it.comment)
                checkBoxLike?.isChecked = it.isOK == 1
                textViewDsc?.text = it.dsc
                imageViewPoster?.let { img ->
                    Glide.with(img.context)
                        .load(it.bigPosterPath)
                        .placeholder(R.drawable.ic_baseline_image_24)
                        .error(R.drawable.ic_baseline_error_24)
                        .into(img)
                }

                it.posterPath?.let {path ->
                    Log.d(MainActivity.TAG, path)
                }
            }
        })
    }

    override fun onDetach() {
        currentFilm?.apply {
            comment = editTextComment?.text.toString()
            checkBoxLike?.let {
                isOK = if (it.isChecked) 1 else 0
            }
            viewModel.updateFilm(this)
        }
        super.onDetach()
    }

    private fun sendNotification(time: Long) {
        currentFilm?.id?.let {
            val launchIntent =
                requireContext().packageManager.getLaunchIntentForPackage("kve.ru.firstproject")
            launchIntent?.putExtra(EXTRA_FILM_ID, it)
            launchIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val contentIntent =
                PendingIntent.getActivity(
                    requireContext(),
                    it,
                    launchIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )

            val mBuilder =
                NotificationCompat.Builder(
                    requireContext(),
                    FilmNotificationPublisher.NOTIFICATION_CHANNEL_ID
                ).apply {
                    setSmallIcon(R.drawable.movie)
                    setAutoCancel(true)
                    setContentTitle(currentFilm?.name)
                    setContentText(currentFilm?.dsc)
                    val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    setSound(alarmSound)
                    setContentIntent(contentIntent)
                }

            // TODO: change to input time parameter
            val futureInMillis = SystemClock.elapsedRealtime() + 10000

            val notificationIntent = Intent(requireContext(), FilmNotificationPublisher::class.java)
            notificationIntent.putExtra(FilmNotificationPublisher.NOTIFICATION_ID, currentFilm?.id)
            notificationIntent.putExtra(FilmNotificationPublisher.NOTIFICATION, mBuilder.build())
            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                it,
                notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

            Log.d(FilmNotificationPublisher.TAG, "Start notify...")
            val alarmManager =
                requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis] = pendingIntent
        }

    }
}