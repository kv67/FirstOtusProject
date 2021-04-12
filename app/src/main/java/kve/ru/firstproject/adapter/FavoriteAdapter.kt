package kve.ru.firstproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kve.ru.firstproject.R
import kve.ru.firstproject.db.Film

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private val dataList = ArrayList<Film>()

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

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewPoster = itemView.findViewById<ImageView>(R.id.imageViewPoster)
        private val textViewName = itemView.findViewById<TextView>(R.id.textViewName)
        private val imageViewStar = itemView.findViewById<ImageView>(R.id.imageViewStar)

        init {
            imageViewStar.visibility = GONE
        }

        fun bind(film: Film) {
            Glide.with(imageViewPoster.context)
                .load(film.posterPath)
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_error_24)
                .into(imageViewPoster)
            textViewName.text = film.name
        }
    }
}