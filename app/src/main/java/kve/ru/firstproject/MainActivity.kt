package kve.ru.firstproject

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kve.ru.firstproject.fragments.FavoriteListFragment
import kve.ru.firstproject.fragments.FilmDetailFragment
import kve.ru.firstproject.fragments.FilmListFragment
import kve.ru.firstproject.model.FilmViewModel
import kve.ru.firstproject.utils.FeatureToggles

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
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
            curView: View?,
            message: String,
            buttonCaption: String,
            listener: (() -> Unit)?
        ) {
            curView?.let {
                Snackbar.make(curView, message, Snackbar.LENGTH_LONG).apply {
                    view.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimaryDark
                        )
                    )
                    setAction(buttonCaption) {
                        listener?.invoke()
                    }
                    show()
                }
            }
        }
    }

    private val remoteConfig = Firebase.remoteConfig
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
        ViewModelProvider(this)[FilmViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        initDrawer()

        viewModel.isSelected.observe(this, { selected ->
            selected?.let {
                if (it) {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragmentContainer,
                            FilmDetailFragment(),
                            FilmDetailFragment.TAG
                        )
                        .addToBackStack(null)
                        .commit()
                }
            }
        })

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
                showSnackBar(
                    findViewById<RecyclerView>(R.id.recyclerViewFilmsFragment),
                    getString(R.string.load_data_error_msg) + it,
                    getString(R.string.repeat_caption)
                ) { viewModel.loadData() }
                viewModel.clearErrors()
            }
        })

        savedInstanceState ?: run {
            viewModel.loadData()
            showFilmList()
        }
    }

    private fun showFilmList() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                FilmListFragment(),
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
        navigationView.menu.findItem(R.id.nav_home).isChecked = true
        navigationView.menu.findItem(R.id.nav_delete_cache).isVisible =
            remoteConfig.getBoolean(FeatureToggles.CACHE_CLEAR_ENABLED)
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.fragments.last()
        if (supportFragmentManager.backStackEntryCount > 0) {
            if (fragment.tag != FilmListFragment.TAG) {
                navigationView.menu.findItem(R.id.nav_home).isChecked = true
                navigationView.menu.findItem(R.id.nav_delete_cache).isEnabled = true
                super.onBackPressed()
            }
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragment = supportFragmentManager.fragments.last()
        when (item.itemId) {
            R.id.nav_home -> {
                if (fragment.tag != FilmListFragment.TAG &&
                    supportFragmentManager.backStackEntryCount > 0
                ) {
                    onBackPressed()
                }
                navigationView.menu.findItem(R.id.nav_delete_cache).isEnabled = true
            }
            R.id.nav_favorites -> {
                if (fragment.tag != FavoriteListFragment.TAG) {
                    if (fragment.tag == FilmDetailFragment.TAG) {
                        onBackPressed()
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragmentContainer,
                            FavoriteListFragment(),
                            FavoriteListFragment.TAG
                        )
                        .addToBackStack(null)
                        .commit()
                }
                navigationView.menu.findItem(R.id.nav_delete_cache).isEnabled = false
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
            R.id.nav_delete_cache -> {
                val bld: AlertDialog.Builder = AlertDialog.Builder(this)
                val lst =
                    DialogInterface.OnClickListener { dialog: DialogInterface, which ->
                        when (which) {
                            BUTTON_NEGATIVE -> dialog.dismiss()
                            BUTTON_POSITIVE -> {
                                viewModel.clearFilms()
                                dialog.dismiss()
                            }
                        }
                    }
                bld.setMessage(getString(R.string.clear_cache_confirmation))
                bld.setTitle(this.getString(R.string.clear_the_cache))
                bld.setNegativeButton(this.getString(R.string.negative_button), lst)
                bld.setPositiveButton(this.getString(R.string.positive_button), lst)
                val dialog: AlertDialog = bld.create()
                dialog.show()
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
