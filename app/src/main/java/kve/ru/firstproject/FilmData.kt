package kve.ru.firstproject

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class FilmData(
    val id: Int, val name: String, val dsc: String, val img: Int,
    var comment: String, var isOK: Boolean
) : Parcelable

