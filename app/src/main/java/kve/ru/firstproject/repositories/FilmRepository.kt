package kve.ru.firstproject.repositories

import kve.ru.firstproject.App
import kve.ru.firstproject.db.Db
import kve.ru.firstproject.db.Film
import kve.ru.firstproject.db.Notification
import kve.ru.firstproject.pojo.BestMovie
import kve.ru.firstproject.utils.NetworkUtils
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class FilmRepository {

    fun saveFilmsToDb(films: List<BestMovie?>, filmListener: ((films: List<Film>) -> Unit)) {
        val task = Runnable {
            for (movie in films) {
                movie?.let {
                    if (!it.title.equals("Your Name")) {
                        var path: String? = movie.posterPath
                        var bigPath: String? = null
                        path?.let { p ->
                            if (!p.startsWith(
                                    NetworkUtils.BASE_POSTER_URL + NetworkUtils.SMALL_POSTER_SIZE
                                )
                            ) {
                                path =
                                    NetworkUtils.BASE_POSTER_URL + NetworkUtils.SMALL_POSTER_SIZE + p
                                bigPath = NetworkUtils.BASE_POSTER_URL +
                                        NetworkUtils.BIG_POSTER_SIZE + p
                            }
                        }
                        val film = Db.getInstance(App.instance)?.getFilmDao()
                            ?.getById(movie.id)
                        film?.let {
                            Db.getInstance(App.instance)?.getFilmDao()
                                ?.updateFilm(
                                    Film(
                                        movie.id,
                                        movie.title,
                                        movie.overview,
                                        path,
                                        bigPath,
                                        (movie.popularity?.times(1000))?.toInt()
                                            ?: 0,
                                        film.isOK,
                                        film.isFavorite,
                                        film.comment
                                    )
                                )
                        } ?: run {
                            Db.getInstance(App.instance)?.getFilmDao()
                                ?.insertFilm(
                                    Film(
                                        movie.id,
                                        movie.title,
                                        movie.overview,
                                        path,
                                        bigPath,
                                        (movie.popularity?.times(1000))?.toInt()
                                            ?: 0
                                    )
                                )
                        }
                    }
                }
            }
            Db.getInstance(App.instance)?.getFilmDao()?.getAll()?.let {
                filmListener.invoke(it)
            }
        }
        Executors.newSingleThreadScheduledExecutor()
            .schedule(task, 20, TimeUnit.MILLISECONDS)
    }

    fun addToFavorite(id: Int, favoriteListener: ((favorites: List<Film>) -> Unit)) {
        val task = Runnable {
            Db.getInstance(App.instance)?.getFilmDao()?.addToFavorite(id)
            Db.getInstance(App.instance)?.getFilmDao()?.getFavorites()?.let {
                favoriteListener.invoke(it)
            }
        }
        Executors.newSingleThreadScheduledExecutor().schedule(task, 20, TimeUnit.MILLISECONDS)
    }

    fun updateFilm(
        film: Film?,
        filmListener: ((films: List<Film>) -> Unit),
        favoriteListener: ((favorites: List<Film>) -> Unit)
    ) {
        val task = Runnable {
            film?.let {
                Db.getInstance(App.instance)?.getFilmDao()?.updateFilm(it)
            }
            Db.getInstance(App.instance)?.getFilmDao()?.getAll()?.let {
                filmListener.invoke(it)
            }
            Db.getInstance(App.instance)?.getFilmDao()?.getFavorites()?.let {
                favoriteListener.invoke(it)
            }
        }
        Executors.newSingleThreadScheduledExecutor().schedule(task, 20, TimeUnit.MILLISECONDS)
    }

    fun getFilmById(id: Int, filmListener: ((film: Film) -> Unit)) {
        val task = Runnable {
            Db.getInstance(App.instance)?.getFilmDao()?.getById(id)?.let {
                filmListener.invoke(it)
            }
        }
        Executors.newSingleThreadScheduledExecutor()
            .schedule(task, 20, TimeUnit.MILLISECONDS)
    }

    fun clearFilms(
        filmListener: ((films: List<Film>) -> Unit),
        favoriteListener: ((favorites: List<Film>) -> Unit),
        notificationListener: ((notes: List<Notification>) -> Unit)
    ) {
        val task = Runnable {
            Db.getInstance(App.instance)?.getFilmDao()?.deleteAll()
            Db.getInstance(App.instance)?.getFilmDao()?.deleteAllNotifications()
            Db.getInstance(App.instance)?.getFilmDao()?.getAll()?.let {
                filmListener.invoke(it)
            }
            Db.getInstance(App.instance)?.getFilmDao()?.getFavorites()?.let {
                favoriteListener.invoke(it)
            }
            Db.getInstance(App.instance)?.getFilmDao()?.getAllNotifications()?.let {
                notificationListener.invoke(it)
            }
        }
        Executors.newSingleThreadScheduledExecutor().schedule(task, 20, TimeUnit.MILLISECONDS)
    }

    fun getNotifications(
        notificationListener: ((notifications: List<Notification>) -> Unit)
    ) {
        val task = Runnable {
            Db.getInstance(App.instance)?.getFilmDao()?.getAllNotifications()?.let {
                notificationListener.invoke(it)
            }
        }
        Executors.newSingleThreadScheduledExecutor()
            .schedule(task, 20, TimeUnit.MILLISECONDS)
    }

    fun addNotification(
        notification: Notification,
        notificationListener: ((notifications: List<Notification>) -> Unit)
    ) {
        val task = Runnable {
            Db.getInstance(App.instance)?.getFilmDao()?.getNotificationById(notification.id)?.let {
                Db.getInstance(App.instance)?.getFilmDao()?.updateNotification(notification)
            } ?: run {
                Db.getInstance(App.instance)?.getFilmDao()?.insertNotification(notification)
            }

            Db.getInstance(App.instance)?.getFilmDao()?.getAllNotifications()?.let {
                notificationListener.invoke(it)
            }
        }
        Executors.newSingleThreadScheduledExecutor()
            .schedule(task, 20, TimeUnit.MILLISECONDS)
    }

    fun deleteNotification(
        id: Int,
        notificationListener: ((notifications: List<Notification>) -> Unit)
    ) {
        val task = Runnable {
            Db.getInstance(App.instance)?.getFilmDao()?.deleteNotificationById(id)
            Db.getInstance(App.instance)?.getFilmDao()?.getAllNotifications()?.let {
                notificationListener.invoke(it)
            }
        }
        Executors.newSingleThreadScheduledExecutor()
            .schedule(task, 20, TimeUnit.MILLISECONDS)
    }

    fun processNotification(id: Int, notificationListener: (() -> Unit)) {
        val task = Runnable {
            val curNotification =
                Db.getInstance(App.instance)?.getFilmDao()?.getNotificationById(id)
            curNotification?.let {
                Db.getInstance(App.instance)?.getFilmDao()
                    ?.deleteNotificationById(id)
                notificationListener.invoke()
            }
        }
        Executors.newSingleThreadScheduledExecutor()
            .schedule(task, 20, TimeUnit.MILLISECONDS)
    }
}