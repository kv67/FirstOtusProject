package kve.ru.firstproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kve.ru.firstproject.R
import kve.ru.firstproject.data.FilmData

class FavoriteAdapter(private val dataList: MutableList<FilmData>) :
    RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewPoster = itemView.findViewById<ImageView>(R.id.imageViewPoster)
        private val textViewName = itemView.findViewById<TextView>(R.id.textViewName)
        private val imageViewStar = itemView.findViewById<ImageView>(R.id.imageViewStar)

        init {
            imageViewStar.visibility = GONE
        }

        fun bind(film: FilmData) {
            imageViewPoster.setImageResource(film.img)
            textViewName.text = film.name
        }
    }

}