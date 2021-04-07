package kve.ru.firstproject.fragments

import android.graphics.Color
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
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import kve.ru.firstproject.App
import kve.ru.firstproject.R
import kve.ru.firstproject.data.FilmData
import kve.ru.firstproject.db.Film
import kve.ru.firstproject.model.FilmViewModel

class FilmDetailFragment : Fragment() {

    companion object {
        const val TAG = "FilmDetailFragment"
        private const val EXTRA_FILM = "EXTRA_FILM"
        private const val EXTRA_FILM_ID = "EXTRA_FILM_ID"

        fun newInstance(id: Int): FilmDetailFragment {
            return FilmDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_FILM_ID, id)
                }
            }
        }
    }

    private var filmData: Film? = null

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

    private val viewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(App.instance)
            .create(FilmViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_film_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.selectedFilm.observe(viewLifecycleOwner, {
            it?.let {
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

//                val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
//                toolbar.setTitleTextAppearance(requireContext(), R.style.ToolbarCustomTheme)
//                toolbar.setTitleMargin(10, 10, 10, 10)
//                toolbar.title = it.name
            }
        })

        arguments?.getInt(EXTRA_FILM_ID)?.let {
            viewModel.getFilmById(it)
        }


//        val filmData = arguments?.getParcelable<Film>(EXTRA_FILM)
//        filmData?.let {
//            requireActivity().title = it.name
//            editTextComment?.setText(it.comment)
//            checkBoxLike?.isChecked = it.isOK == 1
//            textViewDsc?.text = it.dsc
//            imageViewPoster?.let { img ->
//                Glide.with(img.context)
//                    .load(it.bigPosterPath)
//                    .placeholder(R.drawable.ic_baseline_image_24)
//                    .error(R.drawable.ic_baseline_error_24)
//                    .into(img)
//            }
//        }


    }

    fun getComment() = editTextComment?.text

    fun isOk() = checkBoxLike?.isChecked

}