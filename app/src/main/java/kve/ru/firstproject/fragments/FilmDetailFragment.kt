package kve.ru.firstproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
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
            requireActivity().title = it.name
            editTextComment?.setText(it.comment)
            checkBoxLike?.isChecked = it.isOK
            textViewDsc?.text = it.dsc
            imageViewPoster?.let { img ->
                Glide.with(img.context)
                    .load(it.bigPosterPath)
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .error(R.drawable.ic_baseline_error_24)
                    .into(img)
            }
        }

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        filmData?.let {
            toolbar.title = it.name
        } ?: run { toolbar.title = getString(R.string.no_name_film) }
    }

    fun getComment() = editTextComment?.text

    fun isOk() = checkBoxLike?.isChecked

}