package kve.ru.firstproject

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kve.ru.firstproject.MainActivity.Companion.FAVORITES
import kve.ru.firstproject.adapter.FavoriteAdapter
import kve.ru.firstproject.data.FavoriteList
import kve.ru.firstproject.data.FilmData

class FavoriteActivity : AppCompatActivity() {

    private var favorites = ArrayList<FilmData>()
    private val recyclerViewFilms by lazy {
        findViewById<RecyclerView>(R.id.recyclerViewFilms)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        intent?.getParcelableExtra<FavoriteList>(FAVORITES)?.let {
            favorites = it.favorites as ArrayList<FilmData>
        }

        initRecyclerView()
        initTouchHelper()
    }

    private fun getColumnCount(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width: Int = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        return if (width / 185 > 2) width / 185 else 2
    }

    private fun initRecyclerView() {
        val adapter = FavoriteAdapter(favorites)
        recyclerViewFilms.layoutManager = GridLayoutManager(this, getColumnCount())
        recyclerViewFilms.adapter = adapter
    }

    private fun initTouchHelper() {
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
                favorites.removeAt(position)
                recyclerViewFilms.adapter?.notifyItemRemoved(position)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerViewFilms)
    }

    override fun finish() {
        val resultIntent = Intent()
        resultIntent.putExtra(FAVORITES, FavoriteList(favorites))
        setResult(RESULT_OK, resultIntent)
        super.finish()
    }
}