package kve.ru.firstproject.db

import android.content.Context
import androidx.room.Room

object Db {
    private var INSTANCE: AppDb? = null

    fun getInstance(context: Context): AppDb? {
        if (INSTANCE == null) {
            synchronized(AppDb::class) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    AppDb::class.java, "films.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
        }
        return INSTANCE
    }
}