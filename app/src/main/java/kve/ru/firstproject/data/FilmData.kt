package kve.ru.firstproject.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class FilmData(
    val id: Int, val name: String, val dsc: String, val img: Int,
    var comment: String, var isOK: Boolean, var selected: Boolean, var isFavorite: Boolean
) : Parcelable

