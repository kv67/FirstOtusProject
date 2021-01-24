package kve.ru.firstproject

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class SecondActivity : AppCompatActivity() {

    private var currentFilm = FilmData(0, "Empty film", "", 0, "", false)

    companion object {
        const val EXTRA_DATA = "EXTRA_DATA"
    }

    private val imageViewPoster by lazy {
        findViewById<ImageView>(R.id.imageViewPoster)
    }
    private val textViewName by lazy {
        findViewById<TextView>(R.id.textViewName)
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

        val filmData = intent?.getParcelableExtra<FilmData>(EXTRA_DATA)
        filmData?.let {
            currentFilm = it
            editTextComment.setText(currentFilm.comment)
            checkBoxLike.isChecked = currentFilm.isOK
            textViewName.text = currentFilm.name
            textViewDsc.text = currentFilm.dsc
            imageViewPoster.setImageResource(it.img)
        }
    }

    override fun finish() {
        currentFilm.comment = editTextComment.text.toString()
        currentFilm.isOK = checkBoxLike.isChecked
        val resultIntent = Intent()
        resultIntent.putExtra(EXTRA_DATA, currentFilm)
        setResult(RESULT_OK, resultIntent)
        super.finish()
    }
}