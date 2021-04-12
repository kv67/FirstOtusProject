package kve.ru.firstproject.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kve.ru.firstproject.R
import kve.ru.firstproject.adapter.FilmAdapter
import kve.ru.firstproject.model.FilmViewModel
import kve.ru.firstproject.utils.FavoriteItemDecoration
import kve.ru.firstproject.utils.FilmsItemAnimator

class FilmListFragment : Fragment() {

    companion object {
        const val TAG = "FilmListFragment"
    }

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[FilmViewModel::class.java]
    }
    private val pullToRefresh by lazy {
        view?.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
    }

    private var recyclerViewFilms: RecyclerView? = null
    private var filmAdapter: FilmAdapter? = null
    private var filmLayoutManager: GridLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        return inflater.inflate(R.layout.fragment_film_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().title = getString(R.string.app_name)
        setHasOptionsMenu(true)

        recyclerViewFilms = view.findViewById(R.id.recyclerViewFilmsFragment)
        filmAdapter = FilmAdapter(activity as FilmAdapter.OnFilmClickListener)
        filmLayoutManager = GridLayoutManager(requireContext(), getColumnCount())

        recyclerViewFilms?.apply {
            adapter = filmAdapter
            layoutManager = filmLayoutManager
            addItemDecoration(FavoriteItemDecoration(requireContext(), 15))
            itemAnimator = FilmsItemAnimator()
        }

        viewModel.films.observe(viewLifecycleOwner, { films ->
            filmAdapter?.setData(films)
        })

        pullToRefresh?.setOnRefreshListener {
            viewModel.refreshData()
            pullToRefresh?.isRefreshing = false
        }
    }

    private fun getColumnCount(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width: Int = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        return if (width / 185 > 2) width / 185 else 2
    }

    fun getFilmListAdapter(): FilmAdapter? {
        return filmAdapter
    }
}