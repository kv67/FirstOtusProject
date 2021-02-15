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
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kve.ru.firstproject.adapter.FilmAdapter
import kve.ru.firstproject.data.FilmData
import kve.ru.firstproject.data.FilmList
import kve.ru.firstproject.fragments.FavoriteListFragment
import kve.ru.firstproject.fragments.FilmDetailFragment
import kve.ru.firstproject.fragments.FilmListFragment

class MainActivity : AppCompatActivity(), FilmAdapter.OnFilmClickListener,
    FavoriteListFragment.OnRemoveListener, NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val FILMS = "FILMS"
        const val POSITION = "POSITION"
        const val STAR_ANIMATE = "STAR_ANIMATE"
        const val BLOOD_SPORT = 1
        const val COCKTAIL = 2
        const val COMMANDO = 3
        const val EMMANUELLE = 4

        private lateinit var BLOOD_SPORT_BMP: Bitmap
        private lateinit var COCKTAIL_BMP: Bitmap
        private lateinit var COMMANDO_BMP: Bitmap
        private lateinit var EMMANUELLE_BMP: Bitmap

        fun getFilmPoster(id: Int): Bitmap? {
            return when (id) {
                BLOOD_SPORT -> BLOOD_SPORT_BMP
                COCKTAIL -> COCKTAIL_BMP
                COMMANDO -> COMMANDO_BMP
                EMMANUELLE -> EMMANUELLE_BMP
                else -> null
            }
        }

        fun doExit(activity: Activity) {

            val bld: AlertDialog.Builder = AlertDialog.Builder(activity)
            val lst =
                DialogInterface.OnClickListener { dialog: DialogInterface, which ->
                    when (which) {
                        BUTTON_NEGATIVE -> dialog.dismiss()
                        BUTTON_POSITIVE -> activity.finish()
                    }
                }
            bld.setMessage(activity.getString(R.string.ask_exit_conform))
            bld.setTitle(activity.getString(R.string.exit_title))
            bld.setNegativeButton(activity.getString(R.string.negative_button), lst)
            bld.setPositiveButton(activity.getString(R.string.positive_button), lst)
            val dialog: AlertDialog = bld.create()
            dialog.show()
        }

        fun showSnackBar(
            curView: View,
            message: String,
            listener: (() -> Unit)?
        ) {
            Snackbar.make(curView, message, Snackbar.LENGTH_LONG).apply {
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                setAction(context.getString(R.string.undo_btn_title)) {
                    listener?.let { it() }
                }
                show()
            }
        }
    }

    private var curPosition: Int = -1
    private lateinit var films: MutableList<FilmData>
    private val drawer by lazy {
        findViewById<DrawerLayout>(R.id.drawer_layout)
    }
    private val toolbar by lazy {
        findViewById<Toolbar>(R.id.toolbar)
    }
    private val navigationView by lazy {
        findViewById<NavigationView>(R.id.nav_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        initDrawer()

        savedInstanceState?.getParcelable<FilmList>(FILMS)?.let {
            films = it.films
        } ?: run {
            initData()
        }

        savedInstanceState?.getInt(POSITION)?.let {
            curPosition = it
        }

        savedInstanceState ?: run {
            showFilmList()
        }
    }

    private fun showFilmList() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                FilmListFragment.newInstance(films as ArrayList<FilmData>),
                FilmListFragment.TAG
            )
            .commit()
    }

    private fun initDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.open_drawer_dsc, R.string.close_drawer_dsc
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.getHeaderView(0)
            .setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
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
            if (fragment.tag != FilmListFragment.TAG) {
                val menuItem = navigationView.menu.findItem(R.id.nav_home)
                menuItem.isChecked = true
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(FILMS, FilmList(films))
        outState.putInt(POSITION, curPosition)
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
    }

    override fun onStarClick(position: Int) {

        films[position].isFavorite = !films[position].isFavorite
        (supportFragmentManager.findFragmentByTag(FilmListFragment.TAG) as? FilmListFragment)
            ?.notifyItemChanged(
                position,
                STAR_ANIMATE
            )

        showSnackBar(
            findViewById<RecyclerView>(R.id.recyclerViewFilmsFragment),
            if (films[position].isFavorite) getString(R.string.add_to_favorite_msg)
            else getString(R.string.remove_from_favorites_msg)
        ) { onStarClick(position) }
    }

    override fun onRemove(id: Int) {
        val position = films.indexOf(films.firstOrNull { it.id == id })
        if (position > -1) {
            films[position].isFavorite = false
            (supportFragmentManager.findFragmentByTag(FilmListFragment.TAG) as? FilmListFragment)
                ?.notifyItemChanged(
                    position,
                    null
                )
            showSnackBar(
                findViewById<RecyclerView>(R.id.recyclerViewFavoriteFragment),
                getString(R.string.remove_from_favorites_msg)
            ) {
                films[position].isFavorite = true
                (supportFragmentManager.findFragmentByTag(FilmListFragment.TAG) as? FilmListFragment)
                    ?.notifyItemChanged(
                        position,
                        null
                    )
                (supportFragmentManager.findFragmentByTag(FavoriteListFragment.TAG) as? FavoriteListFragment)
                    ?.addRemovedFilm(
                        films[position]
                    )
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragment = supportFragmentManager.fragments.last()
        when (item.itemId) {
            R.id.nav_home -> {
                if (fragment.tag != FilmListFragment.TAG) {
                    onBackPressed()
                }
            }
            R.id.nav_favorites -> {
                if (fragment.tag != FavoriteListFragment.TAG) {
                    if (fragment.tag == FilmDetailFragment.TAG) {
                        onBackPressed()
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragmentContainer,
                            FavoriteListFragment.newInstance(
                                (films.filter { it.isFavorite }) as ArrayList<FilmData>
                            ),
                            FavoriteListFragment.TAG
                        )
                        .addToBackStack(null)
                        .commit()
                }
            }
            R.id.nav_message -> {
                Intent(Intent.ACTION_SEND).apply {
                    this.type = "*/*"
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.inviting))
                    startActivity(Intent.createChooser(this, null))
                }
            }
            R.id.nav_exit -> {
                doExit(this)
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
