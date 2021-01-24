package kve.ru.firstproject

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import kve.ru.firstproject.SecondActivity.Companion.EXTRA_DATA

class MainActivity : AppCompatActivity() {

    companion object {
        const val LOG_TAG = "REQUEST_RESULT"
        const val REQUEST_CODE_EDIT_PROFILE = 1
        const val SELECTED = "SELECTED"
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

    private var selectedFilm = 0
    private var films = ArrayList<FilmData>()
    private val imageViewBloodSport by lazy {
        findViewById<ImageView>(R.id.imageViewBloodSport)
    }
    private val textViewBloodSport by lazy {
        findViewById<TextView>(R.id.textViewBloodSport)
    }
    private val imageViewCocktail by lazy {
        findViewById<ImageView>(R.id.imageViewCocktail)
    }
    private val textViewCocktail by lazy {
        findViewById<TextView>(R.id.textViewCocktail)
    }
    private val imageViewCommando by lazy {
        findViewById<ImageView>(R.id.imageViewCommando)
    }
    private val textViewCommando by lazy {
        findViewById<TextView>(R.id.textViewCommando)
    }
    private val imageViewEmmanuelle by lazy {
        findViewById<ImageView>(R.id.imageViewEmmanuelle)
    }
    private val textViewEmmanuelle by lazy {
        findViewById<TextView>(R.id.textViewEmmanuelle)
    }

    private fun getSelectedData(): FilmData {
        return if (selectedFilm in 1..EMMANUELLE) {
            films[selectedFilm - 1]
        } else {
            FilmData(0, "Empty film", "", 0, "", false)
        }
    }

    private fun setSelection(selectedId: Int) {
        selectedFilm = selectedId

        imageViewBloodSport.background =
            ResourcesCompat.getColor(
                resources,
                if (selectedId == BLOOD_SPORT) R.color.purple_200 else R.color.white, null
            )
                .toDrawable()
        textViewBloodSport.background =
            ResourcesCompat.getColor(
                resources,
                if (selectedId == BLOOD_SPORT) R.color.purple_200 else R.color.white, null
            )
                .toDrawable()
        textViewBloodSport.setTextColor(
            ResourcesCompat.getColor(
                resources,
                if (selectedId == BLOOD_SPORT) R.color.purple_700 else R.color.black, null
            )
        )

        imageViewCocktail.background =
            ResourcesCompat.getColor(
                resources,
                if (selectedId == COCKTAIL) R.color.purple_200 else R.color.white, null
            )
                .toDrawable()
        textViewCocktail.background =
            ResourcesCompat.getColor(
                resources,
                if (selectedId == COCKTAIL) R.color.purple_200 else R.color.white, null
            )
                .toDrawable()
        textViewCocktail.setTextColor(
            ResourcesCompat.getColor(
                resources,
                if (selectedId == COCKTAIL) R.color.purple_700 else R.color.black, null
            )
        )

        imageViewCommando.background =
            ResourcesCompat.getColor(
                resources,
                if (selectedId == COMMANDO) R.color.purple_200 else R.color.white, null
            )
                .toDrawable()
        textViewCommando.background =
            ResourcesCompat.getColor(
                resources,
                if (selectedId == COMMANDO) R.color.purple_200 else R.color.white, null
            )
                .toDrawable()
        textViewCommando.setTextColor(
            ResourcesCompat.getColor(
                resources,
                if (selectedId == COMMANDO) R.color.purple_700 else R.color.black, null
            )
        )

        imageViewEmmanuelle.background =
            ResourcesCompat.getColor(
                resources,
                if (selectedId == EMMANUELLE) R.color.purple_200 else R.color.white, null
            )
                .toDrawable()
        textViewEmmanuelle.background =
            ResourcesCompat.getColor(
                resources,
                if (selectedId == EMMANUELLE) R.color.purple_200 else R.color.white, null
            )
                .toDrawable()
        textViewEmmanuelle.setTextColor(
            ResourcesCompat.getColor(
                resources,
                if (selectedId == EMMANUELLE) R.color.purple_700 else R.color.black, null
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        films.add(
            FilmData(
                BLOOD_SPORT, getString(R.string.blood_sport),
                getString(R.string.blood_sport_dsc), R.drawable.bloodsport, "", false
            )
        )
        films.add(
            FilmData(
                COCKTAIL, getString(R.string.cocktail),
                getString(R.string.cocktail_dsc), R.drawable.cocktail, "", false
            )
        )
        films.add(
            FilmData(
                COMMANDO, getString(R.string.commando),
                getString(R.string.commando_dsc), R.drawable.commando, "", false
            )
        )
        films.add(
            FilmData(
                EMMANUELLE, getString(R.string.emmanuelle),
                getString(R.string.emmanuelle_dsc), R.drawable.emmanuelle, "", false
            )
        )

        savedInstanceState?.getInt(SELECTED)?.let {
            setSelection(it)
        }

        savedInstanceState?.getParcelable<FilmList>(FILMS)?.let {
            films = it.films
        }

        findViewById<View>(R.id.buttonBloodSport).setOnClickListener {
            setSelection(BLOOD_SPORT)
            launchActivity(this, getSelectedData())
        }
        findViewById<View>(R.id.buttonCocktail).setOnClickListener {
            setSelection(COCKTAIL)
            launchActivity(this, getSelectedData())
        }
        findViewById<View>(R.id.buttonCommando).setOnClickListener {
            setSelection(COMMANDO)
            launchActivity(this, getSelectedData())
        }
        findViewById<View>(R.id.buttonEmmanuelle).setOnClickListener {
            setSelection(EMMANUELLE)
            launchActivity(this, getSelectedData())
        }
        findViewById<View>(R.id.buSendMessage).setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                this.type = "*/*"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.inviting))
                startActivity(Intent.createChooser(this, null))
            }
        }
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
        outState.putInt(SELECTED, selectedFilm)
        outState.putParcelable(FILMS, FilmList(films))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK) {
            val filmData = data?.getParcelableExtra<FilmData>(EXTRA_DATA)
            filmData?.let {
                if (it.id in 1..EMMANUELLE) {
                    films[it.id - 1] = it
                }
                Log.d(
                    LOG_TAG,
                    "Фильм ${if (it.isOK) "" else "не "}понравился, комментарий: ${it.comment}"
                )
                Toast.makeText(
                    this,
                    "Фильм ${if (it.isOK) "" else "не "}понравился, комментарий: ${it.comment}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}