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
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kve.ru.firstproject.MainActivity
import kve.ru.firstproject.R
import kve.ru.firstproject.adapter.FilmAdapter
import kve.ru.firstproject.db.Film
import kve.ru.firstproject.model.FilmViewModel
import kve.ru.firstproject.utils.FavoriteItemDecoration
import kve.ru.firstproject.utils.FeatureToggles
import kve.ru.firstproject.utils.FilmsItemAnimator

class FilmListFragment : Fragment() {

    companion object {
        const val TAG = "FilmListFragment"
        const val STAR_ANIMATE = "STAR_ANIMATE"
    }

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[FilmViewModel::class.java]
    }

    private var recyclerViewFilms: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        return inflater.inflate(R.layout.fragment_film_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().title =
            Firebase.remoteConfig.getString(FeatureToggles.APP_TITLE)
        setHasOptionsMenu(true)

        recyclerViewFilms = view.findViewById(R.id.recyclerViewFilmsFragment)
        recyclerViewFilms?.apply {
            adapter = FilmAdapter(
                { id -> viewModel.getFilmById(id) },
                { position -> onStarClick(position) },
                { viewModel.loadData() }
            )
            layoutManager = GridLayoutManager(requireContext(), getColumnCount())
            addItemDecoration(FavoriteItemDecoration(requireContext(), 15))
            itemAnimator = FilmsItemAnimator()
        }

        viewModel.films.observe(viewLifecycleOwner, { films ->
            (recyclerViewFilms?.adapter as FilmAdapter).setData(films)
        })

        val pullToRefresh = view.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        pullToRefresh?.let {
            it.setProgressViewEndTarget(false, 0)
            it.setOnRefreshListener {
                viewModel.refreshData()
                pullToRefresh.isRefreshing = false
            }
        }
    }

    private fun onStarClick(position: Int) {
        val flm: Film? = (recyclerViewFilms?.adapter as FilmAdapter).getItemByPos(position)
        flm?.let {
            it.isFavorite = if (it.isFavorite == 0) 1 else 0
            if (it.isFavorite == 1) {
                viewModel.addToFavorite(it.id)
                recyclerViewFilms?.adapter?.notifyItemChanged(position, STAR_ANIMATE)
            } else {
                viewModel.updateFilm(it)
                MainActivity.showSnackBar(
                    requireView(),
                    getString(R.string.remove_from_favorites_msg),
                    getString(R.string.undo_btn_title)
                ) { onStarClick(position) }
            }
        }
    }

    private fun getColumnCount(): Int {
        val displayMetrics = DisplayMetrics()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = requireActivity().display
            display?.getRealMetrics(displayMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = requireActivity().windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(displayMetrics)
        }

        val width: Int = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        return if (width / 185 > 2) width / 185 else 2
    }
}