package kve.ru.firstproject

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kve.ru.firstproject.MainActivity.Companion.BLOOD_SPORT
import kve.ru.firstproject.MainActivity.Companion.COCKTAIL
import kve.ru.firstproject.MainActivity.Companion.COMMANDO
import kve.ru.firstproject.MainActivity.Companion.EMMANUELLE


class SecondActivity : AppCompatActivity() {

    private var currentFilm = 0

    companion object {
        const val EXTRA_DATA = "EXTRA_DATA"
    }

    private val imageViewPoster by lazy {
        findViewById<ImageView>(R.id.imageViewPoster)
    }
    private val textViewDsc by lazy {
        findViewById<TextView>(R.id.textViewDsc)
    }
    private val checkBoxLike by lazy {
        findViewById<CheckBox>(R.id.checkBoxLike)
    }
    private val editTextComment by lazy {
        findViewById<EditText>(R.id.editTextComment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val filmData = intent?.getParcelableExtra<FilmData>(EXTRA_DATA)
        filmData?.let {
            currentFilm = it.id
            editTextComment.setText(it.comment)
            checkBoxLike.isChecked = it.isOK
            setCurrentFilm(currentFilm)
        }
    }

    private fun setCurrentFilm(currentId: Int) {
        when (currentId) {
            BLOOD_SPORT -> {
                imageViewPoster.setImageResource(R.drawable.bloodsport)
                textViewDsc.text = getString(R.string.blood_sport_dsc)
            }
            COCKTAIL -> {
                imageViewPoster.setImageResource(R.drawable.cocktail)
                textViewDsc.text = getString(R.string.cocktail_dsc)
            }
            COMMANDO -> {
                imageViewPoster.setImageResource(R.drawable.commando)
                textViewDsc.text = getString(R.string.commando_dsc)
            }
            EMMANUELLE -> {
                imageViewPoster.setImageResource(R.drawable.emmanuelle)
                textViewDsc.text = getString(R.string.emmanuelle_dsc)
            }
        }
    }

    override fun finish() {
        val resultIntent = Intent()
        resultIntent.putExtra(
            EXTRA_DATA,
            FilmData(currentFilm, editTextComment.text.toString(), checkBoxLike.isChecked)
        )
        setResult(RESULT_OK, resultIntent)
        super.finish()
    }
}