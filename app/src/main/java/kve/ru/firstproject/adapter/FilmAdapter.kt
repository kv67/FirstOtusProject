package kve.ru.firstproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import kve.ru.firstproject.MainActivity.Companion.getFilmPoster
import kve.ru.firstproject.R
import kve.ru.firstproject.data.FilmData

class FilmAdapter(private val dataList: MutableList<FilmData>, val listener: OnFilmClickListener) :
    RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    interface OnFilmClickListener {
        fun onFilmClick(position: Int)
        fun onStarClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewPoster = itemView.findViewById<ImageView>(R.id.imageViewPoster)
        private val textViewName = itemView.findViewById<TextView>(R.id.textViewName)
        private val imageViewStar = itemView.findViewById<ImageView>(R.id.imageViewStar)

        init {
            imageViewPoster.setOnClickListener {
                listener.onFilmClick(adapterPosition)
            }
            imageViewStar.setOnClickListener {
                listener.onStarClick(adapterPosition)
            }
        }

        fun bind(film: FilmData) {
            imageViewPoster.setImageBitmap(getFilmPoster(film.id))
            imageViewPoster.background =
                ResourcesCompat.getColor(
                    itemView.resources,
                    if (film.selected) R.color.purple_200 else R.color.white, null
                )
                    .toDrawable()
            textViewName.text = film.name
            textViewName.background =
                ResourcesCompat.getColor(
                    itemView.resources,
                    if (film.selected) R.color.purple_200 else R.color.white, null
                )
                    .toDrawable()
            imageViewStar.setImageResource(
                if (film.isFavorite) R.drawable.star_gold else R.drawable.star_silver
            )
        }
    }


}