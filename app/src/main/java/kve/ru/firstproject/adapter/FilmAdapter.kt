package kve.ru.firstproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kve.ru.firstproject.R
import kve.ru.firstproject.data.FilmData
import kve.ru.firstproject.db.Film
import kve.ru.firstproject.model.FilmViewModel

class FilmAdapter(
    private val listener: OnFilmClickListener?
) :
    RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    interface OnFilmClickListener {
        fun onFilmClick(position: Int)
        fun onStarClick(position: Int)
        fun onReachEnd()
    }

    private val dataList = ArrayList<Film>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        if (dataList.size >= 20 && position == FilmViewModel.page * 20 - 8) {
            listener?.onReachEnd()
        }
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun getItemByPos(position: Int): Film? {
        if (dataList.size < position + 1) {
            return null
        }
        return dataList[position]
    }



    fun setData(films: List<Film>) {
        dataList.clear()
        dataList.addAll(films)

        notifyDataSetChanged()
    }

    inner class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewPoster = itemView.findViewById<ImageView>(R.id.imageViewPoster)
        private val textViewName = itemView.findViewById<TextView>(R.id.textViewName)
        val imageViewStar: ImageView = itemView.findViewById(R.id.imageViewStar)

        init {
            imageViewPoster.setOnClickListener {
                listener?.onFilmClick(adapterPosition)
            }
            imageViewStar.setOnClickListener {
                listener?.onStarClick(adapterPosition)
            }
        }

        fun isFavorite(): Boolean = dataList[adapterPosition].isFavorite == 1

        fun bind(film: Film) {
            Glide.with(imageViewPoster.context)
                .load(film.posterPath)
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_error_24)
                .into(imageViewPoster)
            imageViewPoster.background =
                ResourcesCompat.getColor(
                    itemView.resources,
                    R.color.white, null
                )
                    .toDrawable()
            textViewName.text = film.name
            textViewName.background =
                ResourcesCompat.getColor(
                    itemView.resources,
                    R.color.white, null
                )
                    .toDrawable()
            imageViewStar.setImageResource(
                if (film.isFavorite == 1) R.drawable.star_gold else R.drawable.star_silver
            )
        }
    }

}