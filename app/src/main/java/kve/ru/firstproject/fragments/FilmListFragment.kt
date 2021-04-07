package kve.ru.firstproject.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kve.ru.firstproject.App
import kve.ru.firstproject.R
import kve.ru.firstproject.adapter.FilmAdapter
import kve.ru.firstproject.data.FilmData
import kve.ru.firstproject.data.FilmList
import kve.ru.firstproject.model.FilmViewModel
import kve.ru.firstproject.utils.FavoriteItemDecoration
import kve.ru.firstproject.utils.FilmsItemAnimator

class FilmListFragment : Fragment() {

    companion object {
        const val TAG = "FilmListFragment"
        private const val EXTRA_LIST = "EXTRA_LIST"
        private const val CUR_POS = "CUR_POS"
    }

    private val viewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(App.instance)
            .create(FilmViewModel::class.java)
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
        Log.d(TAG, "FRAGMENT CREATED")

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

        savedInstanceState?.getInt(CUR_POS)?.let {
            Log.d(TAG, "SCROLL TO POSITION $it")
            recyclerViewFilms?.layoutManager?.scrollToPosition(it)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "SAVE STATE....")
        filmLayoutManager?.let {
            outState.putInt(
                CUR_POS,
                it.findFirstVisibleItemPosition()
            )
        }
    }

    private fun getColumnCount(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width: Int = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        return if (width / 185 > 2) width / 185 else 2
    }

    fun notifyItemChanged(position: Int, payload: Any?) {
        filmAdapter?.notifyItemChanged(position, payload)
    }

    fun notifyDataSetChanged() {
        filmAdapter?.notifyDataSetChanged()
    }

    fun getFilmListAdapter(): FilmAdapter? {
        return filmAdapter
    }

    fun moveToPosition(position: Int) {
        recyclerViewFilms?.scrollToPosition(position)
    }
}