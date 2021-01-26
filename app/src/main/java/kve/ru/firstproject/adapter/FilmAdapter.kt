package kve.ru.firstproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kve.ru.firstproject.FilmData
import kve.ru.firstproject.R

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

        init {
            imageViewPoster.setOnClickListener {
                listener.onFilmClick(adapterPosition)
            }
        }

        fun bind(film: FilmData) {
            imageViewPoster.setImageResource(film.img)
            textViewName.text = film.name
        }
    }


}