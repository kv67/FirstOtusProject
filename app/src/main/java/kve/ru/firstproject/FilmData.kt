package kve.ru.firstproject

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class FilmData(val id: Int, val comment: String, val isOK: Boolean) : Parcelable