package kve.ru.firstproject.di

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import dagger.Module
import dagger.Provides
import kve.ru.firstproject.db.AppDb
import kve.ru.firstproject.db.FilmDao
import kve.ru.firstproject.db.MIGRATION_2_3
import kve.ru.firstproject.db.MIGRATION_3_4
import kve.ru.firstproject.model.FilmViewModel
import kve.ru.firstproject.model.FilmViewModelFactory
import kve.ru.firstproject.repositories.FilmRepository
import javax.inject.Singleton

@Module
class RoomModule(lApplication: Application, private var lActivity: AppCompatActivity) {
    private val appDatabase: AppDb = Room.databaseBuilder(
        lApplication,
        AppDb::class.java, "films.db"
    )
        .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
        .build()

    @Provides
    @Singleton
    fun providesActivity() = lActivity

    @Provides
    @Singleton
    fun providesRoomDatabase() = appDatabase

    @Provides
    @Singleton
    fun providesFilmDao(appDatabase: AppDb) = appDatabase.getFilmDao()

    @Provides
    @Singleton
    fun providesFilmRepository(filmDao: FilmDao) = FilmRepository(filmDao)

    @Provides
    @Singleton
    fun providesFilmViewModel(lActivity: AppCompatActivity, filmRepository: FilmRepository) =
        ViewModelProvider(
            lActivity,
            FilmViewModelFactory(filmRepository)
        )[FilmViewModel::class.java]

}