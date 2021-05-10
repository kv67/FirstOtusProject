package kve.ru.firstproject.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kve.ru.firstproject.R
import kve.ru.firstproject.db.Film
import kve.ru.firstproject.db.Notification
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
            setDateTime()
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
            }
        })
    }

    private fun setDateTime() {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val dialog = DatePickerDialog(
            requireContext(),
            android.R.style.Theme_Material_Light_Dialog,
            { _: DatePicker, y: Int, m: Int, d: Int ->
                setTime(y, m, d)
            },
            year, month, day
        )
        dialog.datePicker.minDate = cal.timeInMillis
        cal.add(Calendar.DATE, 15)
        dialog.datePicker.maxDate = cal.timeInMillis
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.show()
    }

    private fun setTime(y: Int, m: Int, d: Int) {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val dialog = TimePickerDialog(
            requireContext(),
            android.R.style.Theme_Material_Light_Dialog,
            { _: TimePicker, h: Int, mnt: Int ->
                currentFilm?.let { film ->
                    val dateTime =
                        "${if (d < 10) "0" else ""}$d.${if (m + 1 < 10) "0" else ""}${m + 1}.$y ${if (h < 10) "0" else ""}$h:${if (mnt < 10) "0" else ""}$mnt"
                    Calendar.getInstance().let { cl ->
                        cl.set(y, m, d, h, mnt)
                        if (Calendar.getInstance().timeInMillis < cl.timeInMillis) {
                            viewModel.addNotification(
                                Notification(
                                    film.id,
                                    film.name,
                                    dateTime,
                                    film.dsc
                                )
                            )
                            FilmNotificationPublisher.sendNotification(
                                requireContext(), film.id, film.name, film.dsc, cl.timeInMillis
                            )
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.current_time_passed_msg),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }, hour, minute + 1, true
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.show()
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
}