package kve.ru.firstproject.repositories

import io.reactivex.Completable
import kve.ru.firstproject.db.Film
import kve.ru.firstproject.db.FilmDao
import kve.ru.firstproject.db.Notification
import kve.ru.firstproject.pojo.BestMovie
import javax.inject.Inject

class FilmRepository @Inject constructor(private val filmDao: FilmDao) {

//    companion object {
//        val compositeDisposable = CompositeDisposable()
//    }getFilmById

    fun saveFilmToDb(bestMovie: BestMovie, path: String?, bigPath: String?) =
        Completable.fromAction {
            filmDao.getFilmById(bestMovie.id)?.let { film ->
                filmDao.updateFilm(
                    Film(
                        bestMovie.id,
                        bestMovie.title,
                        bestMovie.overview,
                        path,
                        bigPath,
                        (bestMovie.popularity?.times(1000))?.toInt()
                            ?: 0,
                        film.isOK,
                        film.isFavorite,
                        film.comment
                    )
                )
            } ?: run {
                filmDao.insertFilm(
                    Film(
                        bestMovie.id,
                        bestMovie.title,
                        bestMovie.overview,
                        path,
                        bigPath,
                        (bestMovie.popularity?.times(1000))?.toInt()
                            ?: 0
                    )
                )

            }
        }

    fun addToFavorite(id: Int) = filmDao.addToFavorite(id)

    fun getFilms() = filmDao.getAll()

    fun getFavorites() = filmDao.getFavorites()

    fun updateFilm(film: Film) = filmDao.updateFilm(film)

    fun getFilmById(id: Int) = filmDao.getById(id)

    fun clearFilms(): Completable = Completable.fromAction {
        filmDao.deleteAll()
        filmDao.deleteAllNotifications()
    }

    fun getNotifications() = filmDao.getAllNotifications()

    fun addNotification(notification: Notification) = Completable.fromAction {
        filmDao.getNotificationById(notification.id)
            ?.let {
                filmDao.updateNotification(notification)
            } ?: run {
            filmDao.insertNotification(notification)
        }
    }

    fun deleteNotification(id: Int) = Completable.fromAction {
        filmDao.deleteNotificationById(id)
    }

}