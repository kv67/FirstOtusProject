package kve.ru.firstproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import kve.ru.firstproject.R
import kve.ru.firstproject.data.FilmData

class FilmDetailFragment : Fragment() {

    companion object {
        const val TAG = "FilmDetailFragment"
        private const val EXTRA_FILM = "EXTRA_FILM"

        fun newInstance(film: FilmData): FilmDetailFragment {
            return FilmDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_FILM, film)
                }
            }
        }
    }

    private val imageViewPoster by lazy {
        view?.findViewById<ImageView>(R.id.imageViewPoster)
    }
    private val textViewName by lazy {
        view?.findViewById<TextView>(R.id.textViewName)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_film_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val filmData = arguments?.getParcelable<FilmData>(EXTRA_FILM)
        filmData?.let {
            editTextComment?.setText(it.comment)
            checkBoxLike?.isChecked = it.isOK
            textViewName?.text = it.name
            textViewDsc?.text = it.dsc
            imageViewPoster?.setImageResource(it.img)
        }
    }

    fun getComment() = editTextComment?.text

    fun isOk() = checkBoxLike?.isChecked

}