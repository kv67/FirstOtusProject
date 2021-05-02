package kve.ru.firstproject.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//@Parcelize
@Entity(tableName = "film")
data class Film(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "film_name") var name: String?,
    @ColumnInfo(name = "description") var dsc: String?,
    @ColumnInfo(name = "poster_path") var posterPath: String?,
    @ColumnInfo(name = "big_poster_path") var bigPosterPath: String?,
    var popularity: Int,
    @ColumnInfo(name = "is_ok") var isOK: Int = 0,
    @ColumnInfo(name = "is_favorite") var isFavorite: Int = 0,
    var comment: String = ""
)
