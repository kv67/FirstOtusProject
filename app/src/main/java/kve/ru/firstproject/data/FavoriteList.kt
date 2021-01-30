package kve.ru.firstproject.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class FavoriteList(val favorites: MutableList<FilmData>) : Parcelable