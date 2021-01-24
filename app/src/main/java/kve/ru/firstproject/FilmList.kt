package kve.ru.firstproject

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class FilmList(val films: ArrayList<FilmData>) : Parcelable