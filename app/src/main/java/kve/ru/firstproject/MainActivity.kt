package kve.ru.firstproject

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kve.ru.firstproject.FilmDetailActivity.Companion.EXTRA_DATA
import kve.ru.firstproject.adapter.FilmAdapter
import kve.ru.firstproject.data.FavoriteList
import kve.ru.firstproject.data.FilmData
import kve.ru.firstproject.data.FilmList
import kve.ru.firstproject.fragments.FilmDetailFragment
import kve.ru.firstproject.fragments.FilmListFragment

class MainActivity : AppCompatActivity(), FilmAdapter.OnFilmClickListener {

    companion object {
        const val LOG_TAG = "FILMS_RESULT"
        const val FILMS = "FILMS"
        const val POSITION = "POSITION"
        const val FAVORITES = "FAVORITES"
        const val STAR_ANIMATE = "STAR_ANIMATE"
        const val REQUEST_CODE_EDIT_PROFILE = 1
        const val REQUEST_CODE_EDIT_FAVORITES = 2
        const val BLOOD_SPORT = 1
        const val COCKTAIL = 2
        const val COMMANDO = 3
        const val EMMANUELLE = 4

        private lateinit var BLOOD_SPORT_BMP: Bitmap
        private lateinit var COCKTAIL_BMP: Bitmap
        private lateinit var COMMANDO_BMP: Bitmap
        private lateinit var EMMANUELLE_BMP: Bitmap

        /*fun launchActivity(activity: Activity, selectedData: FilmData) {
            Intent(activity, FilmDetailActivity::class.java).apply {
                putExtra(EXTRA_DATA, selectedData)
                activity.startActivityForResult(this, REQUEST_CODE_EDIT_PROFILE)
            }
        }*/

        fun launchFavoriteActivity(activity: Activity, favorites: MutableList<FilmData>) {
            Intent(activity, FavoriteActivity::class.java).apply {
                putExtra(FAVORITES, FavoriteList(favorites))
                activity.startActivityForResult(this, REQUEST_CODE_EDIT_FAVORITES)
            }
        }

        fun getFilmPoster(id: Int): Bitmap? {
            return when (id) {
                BLOOD_SPORT -> BLOOD_SPORT_BMP
                COCKTAIL -> COCKTAIL_BMP
                COMMANDO -> COMMANDO_BMP
                EMMANUELLE -> EMMANUELLE_BMP
                else -> null
            }
        }
    }

    private var curPosition: Int = -1
    private lateinit var films: MutableList<FilmData>
    private var favorites = ArrayList<FilmData>()
    private val recyclerViewFilms by lazy {
        findViewById<RecyclerView>(R.id.recyclerViewFilms)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        savedInstanceState?.getInt(POSITION)?.let {
            curPosition = it
        }

        savedInstanceState ?: run {
            showFilmList()
        }

        // initRecyclerView()
    }

    /*private fun getColumnCount(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width: Int = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        return if (width / 185 > 2) width / 185 else 2
    }*/

    private fun showFilmList() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                FilmListFragment.newInstance(films as ArrayList<FilmData>),
                FilmListFragment.TAG
            )
            .commit()
    }

    private fun initData() {
        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.bloodsport)
        BLOOD_SPORT_BMP = ThumbnailUtils.extractThumbnail(bitmap, 169, 229)
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.cocktail)
        COCKTAIL_BMP = ThumbnailUtils.extractThumbnail(bitmap, 169, 229)
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.commando)
        COMMANDO_BMP = ThumbnailUtils.extractThumbnail(bitmap, 169, 229)
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.emmanuelle)
        EMMANUELLE_BMP = ThumbnailUtils.extractThumbnail(bitmap, 169, 229)

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

    /*private fun initRecyclerView() {
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
                } else {
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
                recyclerViewFilms.adapter?.notifyItemChanged(position, STAR_ANIMATE)
            }
        })

        recyclerViewFilms.apply {
            layoutManager = GridLayoutManager(this.context, getColumnCount())
            this.adapter = adapter
            recyclerViewFilms.addItemDecoration(FavoriteItemDecoration(applicationContext, 15))
            itemAnimator = FilmsItemAnimator()
        }
    }*/

    override fun onBackPressed() {
        val fragment = supportFragmentManager.fragments.last()
        if (supportFragmentManager.backStackEntryCount > 0) {
            if (fragment.tag == FilmDetailFragment.TAG) {
                (fragment as FilmDetailFragment).getComment()?.let {
                    films[curPosition].comment = it.toString()
                }
                fragment.isOk()?.let {
                    films[curPosition].isOK = it
                }
            }
            super.onBackPressed()
        } else {
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuFavourite -> {
                launchFavoriteActivity(this, favorites)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(FILMS, FilmList(films))
        outState.putParcelable(FAVORITES, FavoriteList(favorites))
        outState.putInt(POSITION, curPosition)
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

    override fun onFilmClick(position: Int) {
        curPosition = position

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                FilmDetailFragment.newInstance(films[position]),
                FilmDetailFragment.TAG
            )
            .addToBackStack(null)
            .commit()

//        films.find { it.selected }?.let {
//            it.selected = false
//            recyclerViewFilms.adapter?.notifyItemChanged(films.indexOf(it))
//        }
//        films[position].selected = true
//        recyclerViewFilms.adapter?.notifyItemChanged(position)
//        launchActivity(this, films[position])
    }

    override fun onStarClick(position: Int) {
        if (films[position].isFavorite) {
            favorites.find { films[position].id == it.id }?.let {
                favorites.remove(it)
            }
        } else {
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
        (supportFragmentManager.findFragmentByTag(FilmListFragment.TAG) as? FilmListFragment)
            ?.notifyItemChanged(
                position,
                STAR_ANIMATE
            )
    }
}
