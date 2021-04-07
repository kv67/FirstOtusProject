package kve.ru.firstproject

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kve.ru.firstproject.adapter.FilmAdapter
import kve.ru.firstproject.data.FilmData
import kve.ru.firstproject.db.Db
import kve.ru.firstproject.db.Film
import kve.ru.firstproject.fragments.FavoriteListFragment
import kve.ru.firstproject.fragments.FilmDetailFragment
import kve.ru.firstproject.fragments.FilmListFragment
import kve.ru.firstproject.model.FilmViewModel
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), FilmAdapter.OnFilmClickListener,
    FavoriteListFragment.OnRemoveListener, NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val TAG = "Main_Activity"
        const val FILMS = "FILMS"
        const val POSITION = "POSITION"
        const val CUR_PAGE = "CUR_PAGE"
        const val STAR_ANIMATE = "STAR_ANIMATE"

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

    private var curPage: Int = 0
    private var curPosition: Int = -1
    private var films: MutableList<FilmData> = ArrayList()
    private val drawer by lazy {
        findViewById<DrawerLayout>(R.id.drawer_layout)
    }
    private val toolbar by lazy {
        findViewById<Toolbar>(R.id.toolbar)
    }
    private val navigationView by lazy {
        findViewById<NavigationView>(R.id.nav_view)
    }
    private val progressBarLoading by lazy {
        findViewById<ProgressBar>(R.id.progressBarLoading)
    }
    private val viewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(FilmViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        initDrawer()

//        savedInstanceState?.getInt(POSITION)?.let {
//            curPosition = it
//        }
//
//        savedInstanceState?.getInt(CUR_PAGE)?.let {
//            curPage = it
//        }
//
//        savedInstanceState?.getParcelable<FilmList>(FILMS)?.let {
//            films = it.films
//        } ?: run {
//            loadData()
//        }

        viewModel.loading.observe(this, {
            if (it) {
                progressBarLoading.visibility = View.VISIBLE
            } else {
                progressBarLoading.visibility = View.INVISIBLE
            }
        })

        viewModel.error.observe(this, {
            progressBarLoading.visibility = View.INVISIBLE
            it?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrors()
            }
        })

        savedInstanceState?.let {
            curPosition = it.getInt(POSITION)
        } ?: run {
            Log.d(TAG, "LOAD DATA")
            viewModel.loadData()
            showFilmList()
        }

    }

    private fun showFilmList() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                FilmListFragment(),
//                FilmListFragment.newInstance(films as ArrayList<FilmData>),
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

    private fun getCurrentFilm(position: Int): Film? {
        getFragmentFilmListAdapter()?.let { adapter ->
            adapter.getItemByPos(position)?.let {
                return it
            }
        }
        return null
    }

    private fun getFragmentFilmListAdapter(): FilmAdapter? {
        (supportFragmentManager.findFragmentByTag(FilmListFragment.TAG) as FilmListFragment?)
            ?.getFilmListAdapter()?.let {
                return it
            }
        return null
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.fragments.last()
        if (supportFragmentManager.backStackEntryCount > 0) {
            if (fragment.tag == FilmDetailFragment.TAG) {
                (fragment as FilmDetailFragment).apply {
                    getCurrentFilm(curPosition)?.let { film ->
                        this.getComment()?.let {
                            film.comment = it.toString()
                        }
                        this.isOk()?.let {
                            film.isOK = if (it) 1 else 0
                        }
                        updateFilm(film)
                    }
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

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(POSITION, curPosition)
    }

    override fun onFilmClick(position: Int) {
        (supportFragmentManager.findFragmentByTag(FilmListFragment.TAG) as FilmListFragment?)
            ?.getFilmListAdapter()?.let { adapter ->
                adapter.getItemByPos(position)?.let { film ->
                    curPosition = position
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragmentContainer,
                            FilmDetailFragment.newInstance(film.id),
                            FilmDetailFragment.TAG
                        )
                        .addToBackStack(null)
                        .commit()
                }
            }
    }

    private fun updateFilm(film: Film) {
        val task = Runnable {
            Db.getInstance(App.instance)?.getFilmDao()?.updateFilm(film)
        }
        Executors.newSingleThreadScheduledExecutor().schedule(task, 20, TimeUnit.MILLISECONDS)
    }

    override fun onStarClick(position: Int) {
        getCurrentFilm(position)?.let {
            it.isFavorite = if (it.isFavorite == 0) 1 else 0
            updateFilm(it)
            getFragmentFilmListAdapter()?.notifyItemChanged(position, STAR_ANIMATE)
            showSnackBar(
                findViewById<RecyclerView>(R.id.recyclerViewFilmsFragment),
                if (it.isFavorite == 1) getString(R.string.add_to_favorite_msg)
                else getString(R.string.remove_from_favorites_msg)
            ) { onStarClick(position) }
        }
    }

    override fun onReachEnd() {
        viewModel.loadData()
    }

    override fun onRemove(id: Int) {
        val position = films.indexOf(films.firstOrNull { it.id == id })
        if (position > -1) {
            films[position].isFavorite = false
            (supportFragmentManager.findFragmentByTag(FilmListFragment.TAG) as FilmListFragment?)
                ?.notifyItemChanged(
                    position,
                    null
                )
            showSnackBar(
                findViewById<RecyclerView>(R.id.recyclerViewFavoriteFragment),
                getString(R.string.remove_from_favorites_msg)
            ) {
                films[position].isFavorite = true
                (supportFragmentManager.findFragmentByTag(FilmListFragment.TAG) as FilmListFragment?)
                    ?.notifyItemChanged(
                        position,
                        null
                    )
                (supportFragmentManager.findFragmentByTag(FavoriteListFragment.TAG) as FavoriteListFragment?)
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
