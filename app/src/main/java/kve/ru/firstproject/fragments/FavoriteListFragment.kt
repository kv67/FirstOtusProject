package kve.ru.firstproject.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kve.ru.firstproject.R
import kve.ru.firstproject.adapter.FavoriteAdapter
import kve.ru.firstproject.model.FilmViewModel
import kve.ru.firstproject.utils.FavoriteItemDecoration

class FavoriteListFragment : Fragment() {

    companion object {
        const val TAG = "FavoriteListFragment"
    }

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[FilmViewModel::class.java]
    }

    private var recyclerViewFavorites: RecyclerView? = null
    private var favoriteAdapter: FavoriteAdapter? = null

    interface OnRemoveListener {
        fun onRemove(position: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        return inflater.inflate(R.layout.fragment_favorite_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().title = getString(R.string.favorites)
        favoriteAdapter = FavoriteAdapter()
        recyclerViewFavorites =
            view.findViewById<RecyclerView>(R.id.recyclerViewFavoriteFragment).apply {
                adapter = favoriteAdapter
                layoutManager = GridLayoutManager(requireContext(), getColumnCount())
                addItemDecoration(FavoriteItemDecoration(requireContext(), 15))
            }

        initTouchHelper {
            (activity as? OnRemoveListener)?.onRemove(it)
        }

        viewModel.favorites.observe(viewLifecycleOwner, { favorites ->
            favoriteAdapter?.setData(favorites)
        })

    }

    private fun initTouchHelper(listener: ((id: Int) -> Unit)?) {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                listener?.invoke(viewHolder.adapterPosition)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerViewFavorites)
    }

    private fun getColumnCount(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width: Int = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        return if (width / 185 > 2) width / 185 else 2
    }

    fun getFavoriteListAdapter(): FavoriteAdapter? {
        return favoriteAdapter
    }
}