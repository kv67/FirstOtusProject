package kve.ru.firstproject.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class FilmList(val films: MutableList<FilmData>) : Parcelable