package kve.ru.firstproject.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Film::class, Notification::class], version = 3)
abstract class AppDb : RoomDatabase() {
    abstract fun getFilmDao(): FilmDao
}