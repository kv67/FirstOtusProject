package kve.ru.firstproject

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable

class MainActivity : AppCompatActivity() {

    companion object {
        const val SELECTED = "SELECTED"
        const val BLOOD_SPORT = 1
        const val COCKTAIL = 2
        const val COMMANDO = 3
        const val EMMANUELLE = 4
    }

    private var selectedFilm = 0
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

    private fun setSelection(selectedId: Int) {
        selectedFilm = selectedId

        imageViewBloodSport.background =
                ResourcesCompat.getColor(resources,
                        if (selectedId == BLOOD_SPORT) R.color.purple_200 else R.color.white, null)
                        .toDrawable()
        textViewBloodSport.background =
                ResourcesCompat.getColor(resources,
                        if (selectedId == BLOOD_SPORT) R.color.purple_200 else R.color.white, null)
                        .toDrawable()
        textViewBloodSport.setTextColor(ResourcesCompat.getColor(resources,
                if (selectedId == BLOOD_SPORT) R.color.purple_700 else R.color.black, null))

        imageViewCocktail.background =
                ResourcesCompat.getColor(resources,
                        if (selectedId == COCKTAIL) R.color.purple_200 else R.color.white, null)
                        .toDrawable()
        textViewCocktail.background =
                ResourcesCompat.getColor(resources,
                        if (selectedId == COCKTAIL) R.color.purple_200 else R.color.white, null)
                        .toDrawable()
        textViewCocktail.setTextColor(ResourcesCompat.getColor(resources,
                if (selectedId == COCKTAIL) R.color.purple_700 else R.color.black, null))

        imageViewCommando.background =
                ResourcesCompat.getColor(resources,
                        if (selectedId == COMMANDO) R.color.purple_200 else R.color.white, null)
                        .toDrawable()
        textViewCommando.background =
                ResourcesCompat.getColor(resources,
                        if (selectedId == COMMANDO) R.color.purple_200 else R.color.white, null)
                        .toDrawable()
        textViewCommando.setTextColor(ResourcesCompat.getColor(resources,
                if (selectedId == COMMANDO) R.color.purple_700 else R.color.black, null))

        imageViewEmmanuelle.background =
                ResourcesCompat.getColor(resources,
                        if (selectedId == EMMANUELLE) R.color.purple_200 else R.color.white, null)
                        .toDrawable()
        textViewEmmanuelle.background =
                ResourcesCompat.getColor(resources,
                        if (selectedId == EMMANUELLE) R.color.purple_200 else R.color.white, null)
                        .toDrawable()
        textViewEmmanuelle.setTextColor(ResourcesCompat.getColor(resources,
                if (selectedId == EMMANUELLE) R.color.purple_700 else R.color.black, null))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.getInt(SELECTED)?.let {
            setSelection(it)
        }

        findViewById<View>(R.id.buttonBloodSport).setOnClickListener {
            setSelection(BLOOD_SPORT)
        }
        findViewById<View>(R.id.buttonCocktail).setOnClickListener {
            setSelection(COCKTAIL)
        }
        findViewById<View>(R.id.buttonCommando).setOnClickListener {
            setSelection(COMMANDO)
        }
        findViewById<View>(R.id.buttonEmmanuelle).setOnClickListener {
            setSelection(EMMANUELLE)
        }
        findViewById<View>(R.id.buSendMessage).setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                 this.type = "*/*"
                 putExtra(Intent.EXTRA_TEXT,  getString(R.string.inviting))
                  startActivity(Intent.createChooser(this, null))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SELECTED, selectedFilm)
    }
}