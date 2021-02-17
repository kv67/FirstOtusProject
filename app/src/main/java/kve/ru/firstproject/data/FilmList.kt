package kve.ru.firstproject.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilmList(val films: MutableList<FilmData>) : Parcelable