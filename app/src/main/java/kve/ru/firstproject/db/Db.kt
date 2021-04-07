package kve.ru.firstproject.db

import android.content.Context
import android.util.Log
import androidx.room.Room
import kve.ru.firstproject.model.FilmViewModel

object Db {
    private var INSTANCE: AppDb? = null

    fun getInstance(context: Context): AppDb? {
        if (INSTANCE == null) {
            synchronized(AppDb::class) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    AppDb::class.java, "films.db"
                )
                    /*.allowMainThreadQueries()*/
                    .fallbackToDestructiveMigration()
                    // .addMigrations(MIGRATION_1_2)
                    // .addCallback(DbCallback(context))
                    .build()
            }
        }
        return INSTANCE
    }

    fun destroyInstance() {
        INSTANCE?.close()
        INSTANCE = null
    }

}