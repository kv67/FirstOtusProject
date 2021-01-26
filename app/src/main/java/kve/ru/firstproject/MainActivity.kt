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
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kve.ru.firstproject.SecondActivity.Companion.EXTRA_DATA
import kve.ru.firstproject.adapter.FilmAdapter

class MainActivity : AppCompatActivity() {

    companion object {
        const val LOG_TAG = "REQUEST_RESULT"
        const val REQUEST_CODE_EDIT_PROFILE = 1
        const val FILMS = "FILMS"
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
    }

    private lateinit var films: MutableList<FilmData>
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
                getString(R.string.blood_sport_dsc), R.drawable.bloodsport, "", false
            ),
            FilmData(
                COCKTAIL, getString(R.string.cocktail),
                getString(R.string.cocktail_dsc), R.drawable.cocktail, "", false
            ),
            FilmData(
                COMMANDO, getString(R.string.commando),
                getString(R.string.commando_dsc), R.drawable.commando, "", false
            ),
            FilmData(
                EMMANUELLE, getString(R.string.emmanuelle),
                getString(R.string.emmanuelle_dsc), R.drawable.emmanuelle, "", false
            )
        )
    }

    private fun initRecyclerView() {
        val adapter = FilmAdapter(films, object : FilmAdapter.OnFilmClickListener {
            override fun onFilmClick(position: Int) {
                launchActivity(this@MainActivity, films[position])
            }

            override fun onStarClick(position: Int) {
                TODO("Not yet implemented")
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(FILMS, FilmList(films))
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
    }
}