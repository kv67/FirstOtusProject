package kve.ru.firstproject.db

import androidx.room.Database
import androidx.room.RoomDatabase

const val TAG = "AppDb"

@Database(entities = [Film::class], version = 2)
abstract class AppDb: RoomDatabase() {
    abstract fun getFilmDao(): FilmDao
}