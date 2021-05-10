package kve.ru.firstproject

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.DialogInterface.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kve.ru.firstproject.fragments.FavoriteListFragment
import kve.ru.firstproject.fragments.FilmDetailFragment
import kve.ru.firstproject.fragments.FilmListFragment
import kve.ru.firstproject.fragments.NotificationListFragment
import kve.ru.firstproject.model.FilmViewModel
import kve.ru.firstproject.service.FilmNotificationPublisher
import kve.ru.firstproject.utils.FeatureToggles
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val TAG = "Main_activity"
        const val MESSAGE_EVENT = "kve.ru.firstproject.MESSAGE_EVENT"

        fun doExit(activity: Activity) {
            val bld: AlertDialog.Builder = AlertDialog.Builder(activity)
            val lst =
                OnClickListener { dialog: DialogInterface, which ->
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

    private val onEvent: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                it.getStringExtra(FilmNotificationPublisher.FILM_ID)?.let { id ->
                    context?.let { cnt ->
                        FilmNotificationPublisher.sendFilmNotification(
                            cnt,
                            id.toInt(),
                            Calendar.getInstance().timeInMillis + 1000,
                            true
                        )
                    }
                    it.removeExtra(FilmNotificationPublisher.FILM_ID)
                }

                var title = ""
                it.getStringExtra(FilmNotificationPublisher.FILM_TITLE)?.let { filmTitle ->
                    title = filmTitle
                    Log.d(TAG, "Received msg -> film title: $title")
                    it.removeExtra(FilmNotificationPublisher.FILM_TITLE)
                }
                var dsc = ""
                it.getStringExtra(FilmNotificationPublisher.FILM_DSC)?.let { filmDsc ->
                    dsc = filmDsc
                    it.removeExtra(FilmNotificationPublisher.FILM_DSC)
                }

                if (title.isNotEmpty()) {
                    showExtraFilmData(
                        title,
                        dsc,
                        it.getStringExtra(FilmNotificationPublisher.FILM_POSTER)
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        initDrawer()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(onEvent, IntentFilter(MESSAGE_EVENT))

        viewModel.isSelected.observe(this, { selected ->
            selected?.let {
                if (it) {
                    showFilmDetail()
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

        intent?.let {
            it.getIntExtra(FilmDetailFragment.EXTRA_FILM_ID, 0).let { id ->
                Log.d(TAG, "EXTRA_FILM_ID = $id")
                if (id > 0) {
                    viewModel.getFilmById(id)
                    showFilmDetail()
                }
                it.removeExtra(FilmDetailFragment.EXTRA_FILM_ID)
            }

            it.getStringExtra(FilmNotificationPublisher.FILM_ID)?.let { id ->
                Log.d(TAG, "FILM_ID = $id")
                if (id.isNotEmpty()) {
                    viewModel.getFilmById(id.toInt())
                    showFilmDetail()
                }
                it.removeExtra(FilmNotificationPublisher.FILM_ID)
            }

            it.getStringExtra(FilmNotificationPublisher.FILM_TITLE)?.let { title ->
                Log.d(TAG, "Firebase film title: $title")
                var dsc = ""
                it.getStringExtra(FilmNotificationPublisher.FILM_DSC)?.let { filmDsc ->
                    dsc = filmDsc
                }

                showExtraFilmData(
                    title, dsc, it.getStringExtra(FilmNotificationPublisher.FILM_POSTER)
                )

                it.removeExtra(FilmNotificationPublisher.FILM_TITLE)
            }
        }
    }

    private fun showExtraFilmData(title: String, dsc: String, poster: String?) {
        val bld: AlertDialog.Builder = AlertDialog.Builder(this)
        bld.setNeutralButton(getString(R.string.button_text_ok)) { dialog: DialogInterface, which ->
            when (which) {
                BUTTON_NEUTRAL -> dialog.dismiss()
            }
        }
        bld.setTitle(title)
        poster?.let {
            val view: View =
                LayoutInflater.from(this).inflate(R.layout.alert_dialog_view, null)
            val filmDsc = view.findViewById<TextView>(R.id.textViewMsgDsc)
            filmDsc.text = dsc
            val image = view.findViewById<ImageView>(R.id.dialog_image_view)
            Glide.with(applicationContext)
                .load(it)
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_error_24)
                .into(image)
            bld.setView(view)
        } ?: run {
            bld.setMessage(dsc)
        }
        val dialog: AlertDialog = bld.create()
        dialog.show()
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

    private fun showFilmDetail() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                FilmDetailFragment(),
                FilmDetailFragment.TAG
            )
            .addToBackStack(null)
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
            Firebase.remoteConfig.getBoolean(FeatureToggles.CACHE_CLEAR_ENABLED)
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
                OnClickListener { dialog: DialogInterface, which ->
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
                if (navigationView.menu.findItem(R.id.nav_delete_cache).isVisible) {
                    navigationView.menu.findItem(R.id.nav_delete_cache).isEnabled = true
                }
            }
            R.id.nav_favorites -> {
                if (fragment.tag != FavoriteListFragment.TAG) {
                    if (fragment.tag != FilmListFragment.TAG &&
                        supportFragmentManager.backStackEntryCount > 0
                    ) {
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
            R.id.nav_notifications -> {
                if (fragment.tag != NotificationListFragment.TAG) {
                    if (fragment.tag != FilmListFragment.TAG &&
                        supportFragmentManager.backStackEntryCount > 0
                    ) {
                        onBackPressed()
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragmentContainer,
                            NotificationListFragment(),
                            NotificationListFragment.TAG
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
                    OnClickListener { dialog: DialogInterface, which ->
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
