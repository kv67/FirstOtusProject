package kve.ru.firstproject.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kve.ru.firstproject.MainActivity
import kve.ru.firstproject.R
import kve.ru.firstproject.adapter.FavoriteAdapter
import kve.ru.firstproject.di.DaggerAppComponent
import kve.ru.firstproject.di.RoomModule
import kve.ru.firstproject.model.FilmViewModel
import kve.ru.firstproject.utils.FavoriteItemDecoration
import javax.inject.Inject

class FavoriteListFragment : Fragment() {

    companion object {
        const val TAG = "FavoriteListFragment"
    }

    @Inject
    lateinit var viewModel: FilmViewModel

    private var recyclerViewFavorites: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().title = getString(R.string.favorites)
        recyclerViewFavorites =
            view.findViewById<RecyclerView>(R.id.recyclerViewFavoriteFragment).apply {
                adapter = FavoriteAdapter()
                layoutManager = GridLayoutManager(requireContext(), getColumnCount())
                addItemDecoration(FavoriteItemDecoration(requireContext(), 15))
            }

        DaggerAppComponent.builder()  //.appModule(AppModule(requireActivity().application))
            .roomModule(
                RoomModule(
                    requireActivity().application,
                    requireActivity() as MainActivity
                )
            ).build()
            .inject(this)

        initTouchHelper {
            (recyclerViewFavorites?.adapter as FavoriteAdapter).getItemByPos(it)?.let { film ->
                film.isFavorite = 0
                viewModel.updateFilm(film)
                MainActivity.showSnackBar(
                    requireView(),
                    getString(R.string.remove_from_favorites_msg),
                    getString(R.string.undo_btn_title)
                ) {
                    film.isFavorite = 1
                    viewModel.updateFilm(film)
                }
            }
        }

        viewModel.favorites.observe(viewLifecycleOwner, { favorites ->
            (recyclerViewFavorites?.adapter as FavoriteAdapter).setData(favorites)
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