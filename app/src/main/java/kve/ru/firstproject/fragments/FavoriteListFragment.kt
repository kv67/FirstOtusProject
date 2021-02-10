package kve.ru.firstproject.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kve.ru.firstproject.R
import kve.ru.firstproject.adapter.FavoriteAdapter
import kve.ru.firstproject.data.FilmData
import kve.ru.firstproject.data.FilmList
import kve.ru.firstproject.utils.FavoriteItemDecoration

class FavoriteListFragment : Fragment() {

    companion object {
        const val TAG = "FavoriteListFragment"
        private const val FAVORITE_LIST = "FAVORITE_LIST"

        fun newInstance(data: ArrayList<FilmData>): FavoriteListFragment {
            return FavoriteListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(FAVORITE_LIST, FilmList(data))
                }
            }
        }
    }

    private var recyclerViewFavorites: RecyclerView? = null
    private var favorites = ArrayList<FilmData>()

    interface OnRemoveListener {
        fun onRemove(id: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewFavorites =
            view.findViewById<RecyclerView>(R.id.recyclerViewFavoriteFragment).apply {
                favorites =
                    (arguments?.getParcelable<FilmList>(FAVORITE_LIST)?.films as ArrayList)
                adapter = FavoriteAdapter(favorites)
                layoutManager = GridLayoutManager(requireContext(), getColumnCount())
                addItemDecoration(FavoriteItemDecoration(requireContext(), 15))
            }
        initTouchHelper() {
            (activity as? OnRemoveListener)?.onRemove(it)
        }
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
                val position = viewHolder.adapterPosition

                val bld: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
                val lst =
                    DialogInterface.OnClickListener { dialog: DialogInterface, which ->
                        when (which) {
                            DialogInterface.BUTTON_NEGATIVE -> {
                                recyclerViewFavorites?.adapter?.notifyDataSetChanged()
                                dialog.dismiss()
                            }
                            DialogInterface.BUTTON_POSITIVE -> {
                                listener?.invoke(
                                    (recyclerViewFavorites?.adapter as FavoriteAdapter).getCurrentFilmId(
                                        position
                                    )
                                )
                                favorites.removeAt(position)
                                recyclerViewFavorites?.adapter?.notifyItemRemoved(position)
                                dialog.dismiss()
                            }
                        }
                    }
                bld.setMessage(getString(R.string.favorite_remove_conform))
                bld.setTitle(getString(R.string.favorites_removing_title))
                bld.setNegativeButton(getString(R.string.negative_button), lst)
                bld.setPositiveButton(getString(R.string.positive_button), lst)
                val dialog: AlertDialog = bld.create()
                dialog.show()
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

}