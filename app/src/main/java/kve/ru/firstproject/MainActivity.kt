package kve.ru.firstproject

import android.app.Activity
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
    private var bloodSportData = FilmData(BLOOD_SPORT, "", false)
    private var cocktailData = FilmData(COCKTAIL, "", false)
    private var commandoData = FilmData(COMMANDO, "", false)
    private var emmanuelleData = FilmData(EMMANUELLE, "", false)
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
        when (selectedFilm) {
            BLOOD_SPORT -> return bloodSportData
            COCKTAIL -> return cocktailData
            COMMANDO -> return commandoData
            EMMANUELLE -> return emmanuelleData
            else -> return FilmData(0, "", false)
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

        savedInstanceState?.getInt(SELECTED)?.let {
            setSelection(it)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SELECTED, selectedFilm)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK) {
            val filmData = data?.getParcelableExtra<FilmData>(EXTRA_DATA)
            filmData?.let {
                when (it.id) {
                    BLOOD_SPORT -> bloodSportData = it
                    COCKTAIL -> cocktailData = it
                    COMMANDO -> commandoData= it
                    EMMANUELLE -> emmanuelleData = it
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