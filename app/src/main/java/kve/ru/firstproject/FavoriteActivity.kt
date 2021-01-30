package kve.ru.firstproject

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import kve.ru.firstproject.MainActivity.Companion.FAVORITES
import kve.ru.firstproject.adapter.FavoriteAdapter
import kve.ru.firstproject.data.FavoriteList
import kve.ru.firstproject.data.FilmData
import kve.ru.firstproject.utils.FavoriteItemDecoration

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
        val decorator = FavoriteItemDecoration(applicationContext, 0)
        recyclerViewFilms.addItemDecoration(decorator)
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

                val bld: AlertDialog.Builder = AlertDialog.Builder(this@FavoriteActivity)
                val lst =
                    DialogInterface.OnClickListener { dialog: DialogInterface, which ->
                        when (which) {
                            DialogInterface.BUTTON_NEGATIVE -> {
                                recyclerViewFilms.adapter?.notifyDataSetChanged()
                                dialog.dismiss()
                            }
                            DialogInterface.BUTTON_POSITIVE -> {
                                favorites.removeAt(position)
                                recyclerViewFilms.adapter?.notifyItemRemoved(position)
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

        itemTouchHelper.attachToRecyclerView(recyclerViewFilms)
    }

    override fun finish() {
        val resultIntent = Intent()
        resultIntent.putExtra(FAVORITES, FavoriteList(favorites))
        setResult(RESULT_OK, resultIntent)
        super.finish()
    }

}