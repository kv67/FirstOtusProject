package kve.ru.firstproject.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kve.ru.firstproject.R
import kve.ru.firstproject.adapter.FilmAdapter
import kve.ru.firstproject.data.FavoriteList
import kve.ru.firstproject.data.FilmData
import kve.ru.firstproject.data.FilmList
import kve.ru.firstproject.utils.FavoriteItemDecoration
import kve.ru.firstproject.utils.FilmsItemAnimator

class FilmListFragment : Fragment() {

    companion object {
        const val TAG = "FilmListFragment"
        private const val EXTRA_LIST = "EXTRA_LIST"

        fun newInstance(data: ArrayList<FilmData>): FilmListFragment {
            return FilmListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_LIST, FilmList(data))
                }
            }
        }
    }

    private var recyclerViewFilms: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_film_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewFilms = view.findViewById<RecyclerView>(R.id.recyclerViewFilmsFragment).apply {
            val films = arguments?.getParcelable<FilmList>(EXTRA_LIST)?.films ?: ArrayList<FilmData>()
            adapter = FilmAdapter(films, (activity as? FilmAdapter.OnFilmClickListener))
            layoutManager = GridLayoutManager(requireContext(), getColumnCount())
            addItemDecoration(FavoriteItemDecoration(requireContext(), 15))
            itemAnimator = FilmsItemAnimator()
        }
    }

    private fun getColumnCount(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width: Int = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        return if (width / 185 > 2) width / 185 else 2
    }

    fun notifyItemChanged(position: Int, payload: Any?) {
        recyclerViewFilms?.adapter?.notifyItemChanged(position, payload)
    }
}