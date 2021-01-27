package kve.ru.firstproject

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kve.ru.firstproject.SecondActivity.Companion.EXTRA_DATA
import kve.ru.firstproject.adapter.FilmAdapter
import kve.ru.firstproject.data.FavoriteList
import kve.ru.firstproject.data.FilmData
import kve.ru.firstproject.data.FilmList

class MainActivity : AppCompatActivity() {

    companion object {
        const val LOG_TAG = "FILMS_RESULT"
        const val REQUEST_CODE_EDIT_PROFILE = 1
        const val REQUEST_CODE_EDIT_FAVORITES = 2
        const val FILMS = "FILMS"
        const val FAVORITES = "FAVORITES"
        const val BLOOD_SPORT = 1
        const val COCKTAIL = 2
        const val COMMANDO = 3
        const val EMMANUELLE = 4

        fun launchActivity(activity: Activity, selectedData: FilmData) {
            Intent(activity, SecondActivity::class.java).apply {
                putExtra(EXTRA_DATA, selectedData)
                activity.startActivityForResult(this, REQUEST_CODE_EDIT_PROFILE)
            }
        }

        fun launchFavoriteActivity(activity: Activity, favorites: MutableList<FilmData>) {
            Intent(activity, FavoriteActivity::class.java).apply {
                putExtra(FAVORITES, FavoriteList(favorites))
                activity.startActivityForResult(this, REQUEST_CODE_EDIT_FAVORITES)
            }
        }
    }

    private lateinit var films: MutableList<FilmData>
    private var favorites = ArrayList<FilmData>()
    private val recyclerViewFilms by lazy {
        findViewById<RecyclerView>(R.id.recyclerViewFilms)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_new)

        findViewById<View>(R.id.buSendMessage).setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                this.type = "*/*"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.inviting))
                startActivity(Intent.createChooser(this, null))
            }
        }

        savedInstanceState?.getParcelable<FilmList>(FILMS)?.let {
            films = it.films
        } ?: run {
            initData()
        }
        savedInstanceState?.getParcelable<FavoriteList>(FAVORITES)?.let {
            favorites = it.favorites as ArrayList<FilmData>
        } ?: run {
            initData()
        }

        initRecyclerView()
    }

    private fun getColumnCount(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width: Int = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        return if (width / 185 > 2) width / 185 else 2
    }

    private fun initData() {
        films = mutableListOf(
            FilmData(
                BLOOD_SPORT, getString(R.string.blood_sport),
                getString(R.string.blood_sport_dsc), R.drawable.bloodsport,
                "", isOK = false, selected = false, isFavorite = false
            ),
            FilmData(
                COCKTAIL, getString(R.string.cocktail),
                getString(R.string.cocktail_dsc), R.drawable.cocktail,
                "", isOK = false, selected = false, isFavorite = false
            ),
            FilmData(
                COMMANDO, getString(R.string.commando),
                getString(R.string.commando_dsc), R.drawable.commando,
                "", isOK = false, selected = false, isFavorite = false
            ),
            FilmData(
                EMMANUELLE, getString(R.string.emmanuelle),
                getString(R.string.emmanuelle_dsc), R.drawable.emmanuelle,
                "", isOK = false, selected = false, isFavorite = false
            )
        )
    }

    private fun initRecyclerView() {
        val adapter = FilmAdapter(films, object : FilmAdapter.OnFilmClickListener {
            override fun onFilmClick(position: Int) {
                films.find { it.selected }?.let {
                    it.selected = false
                    recyclerViewFilms.adapter?.notifyItemChanged(films.indexOf(it))
                }
                films[position].selected = true
                recyclerViewFilms.adapter?.notifyItemChanged(position)
                launchActivity(this@MainActivity, films[position])
            }

            override fun onStarClick(position: Int) {
                if (films[position].isFavorite) {
                    favorites.find { films[position].id == it.id }?.let {
                        favorites.remove(it)
                    }
                } else if (!films[position].isFavorite) {  //  && !favorites.contains(films[position])
                    favorites.find { films[position].id == it.id }?.let {
                        Log.d(
                            LOG_TAG,
                            "Film with id: ${films[position].id} is already in favorites"
                        )
                    } ?: run {
                        favorites.add(films[position])
                    }
                }
                films[position].isFavorite = !films[position].isFavorite
                recyclerViewFilms.adapter?.notifyItemChanged(position)
                Log.d(LOG_TAG, "Size of favorites: ${favorites.size}")
            }

        })
        recyclerViewFilms.layoutManager = GridLayoutManager(this, getColumnCount())
        recyclerViewFilms.adapter = adapter
    }

    override fun onBackPressed() {
        val bld: AlertDialog.Builder = AlertDialog.Builder(this)
        val lst =
            DialogInterface.OnClickListener { dialog: DialogInterface, which ->
                when (which) {
                    BUTTON_NEGATIVE -> dialog.dismiss()
                    BUTTON_POSITIVE -> super.onBackPressed()
                }
            }
        bld.setMessage(getString(R.string.ask_exit_conform))
        bld.setTitle(getString(R.string.exit_title))
        bld.setNegativeButton(getString(R.string.negative_button), lst)
        bld.setPositiveButton(getString(R.string.positive_button), lst)
        val dialog: AlertDialog = bld.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuFavourite) {
            launchFavoriteActivity(this, favorites)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(FILMS, FilmList(films))
        outState.putParcelable(FAVORITES, FavoriteList(favorites))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK) {
            val filmData = data?.getParcelableExtra<FilmData>(EXTRA_DATA)
            filmData?.let {
                if (it.id in 1..EMMANUELLE) {
                    films[it.id - 1] = it
                    recyclerViewFilms.adapter?.notifyItemChanged(it.id - 1)
                }
                Log.d(
                    LOG_TAG,
                    "Фильм ${if (it.isOK) "" else "не "}понравился, комментарий: ${it.comment}"
                )
            }
        }

        if (requestCode == REQUEST_CODE_EDIT_FAVORITES && resultCode == RESULT_OK) {
            val newFavorites = data?.getParcelableExtra<FavoriteList>(FAVORITES)
            newFavorites?.let { obj ->
                favorites = obj.favorites as ArrayList<FilmData>
                for (film: FilmData in films) {
                    favorites.find { it.id == film.id }?.let {
                        film.isFavorite = true
                    } ?: run {
                        film.isFavorite = false
                    }
                }
                recyclerViewFilms.adapter?.notifyDataSetChanged()
            }
        }
    }
}