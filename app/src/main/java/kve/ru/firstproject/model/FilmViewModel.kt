package kve.ru.firstproject.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kve.ru.firstproject.db.Film
import kve.ru.firstproject.db.Notification
import kve.ru.firstproject.pojo.BestMovie
import kve.ru.firstproject.repositories.ApiRepository
import kve.ru.firstproject.repositories.FilmRepository
import kve.ru.firstproject.service.FilmNotificationPublisher
import kve.ru.firstproject.utils.NetworkUtils
import javax.inject.Inject

class FilmViewModel @Inject constructor(private val filmRepository: FilmRepository) : ViewModel() {

    private val filmsLiveData = MutableLiveData<List<Film>>()
    private val favoritesLiveData = MutableLiveData<List<Film>>()
    private val notificationsLiveData = MutableLiveData<List<Notification>>()
    private val selectedFilmData = MutableLiveData<Film>()
    private val isSelectedLiveData = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<String>()
    private val loadingLiveData = MutableLiveData<Boolean>()
    private val favoriteUpdatedLiveData = MutableLiveData<Int>()
    private val compositeDisposable = CompositeDisposable()

    init {
        updateFilms()
        updateFavorites()
        getNotifications()
        loadingLiveData.postValue(false)
        favoriteUpdatedLiveData.postValue(0)
    }

    companion object {
        var page = 0
        const val TAG = "FilmViewModel"
    }

    private val apiRepository by lazy {
        ApiRepository()
    }

    val films: LiveData<List<Film>>
        get() = filmsLiveData

    val favorites: LiveData<List<Film>>
        get() = favoritesLiveData

    val notifications: LiveData<List<Notification>>
        get() = notificationsLiveData

    val selectedFilm: LiveData<Film>
        get() = selectedFilmData

    val isSelected: LiveData<Boolean>
        get() = isSelectedLiveData

    val error: LiveData<String>
        get() = errorLiveData

    val loading: LiveData<Boolean>
        get() = loadingLiveData

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    private fun updateFilms() {
        filmRepository.getFilms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { films ->
                filmsLiveData.postValue(films)
            }?.let { compositeDisposable.add(it) }
    }

    private fun updateFavorites() {
        filmRepository.getFavorites()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { favorites ->
                favoritesLiveData.postValue(favorites)
            }?.let { compositeDisposable.add(it) }
    }

    fun clearErrors() {
        errorLiveData.postValue(null)
    }

    fun refreshData() {
        page = 0
        loadData()
    }

    fun loadData() {
        loadingLiveData.postValue(true)
        compositeDisposable.add(
            apiRepository.loadData(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        response?.apply {
                            response.movies?.let {
                                saveFilmsToDb(it)
                                page++
                            }
                            loadingLiveData.postValue(false)
                        }
                    },
                    { throwable ->
                        errorLiveData.postValue(throwable.message)
                        loadingLiveData.postValue(false)
                    }
                )
        )
    }

    private fun saveFilmsToDb(movies: List<BestMovie?>) {
        for (movie in movies) {
            movie?.let { best ->
                if (!best.title.equals("Your Name")) {
                    var path: String? = best.posterPath
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
                    compositeDisposable.add(
                        filmRepository.saveFilmToDb(best, path, bigPath)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                {
                                    updateFilms()
                                    updateFavorites()
                                },
                                { throwable ->
                                    Log.d(TAG, "Save films to db error: ${throwable.message}")
                                }
                            )
                    )
                }
            }
        }
    }

    private fun getNotifications() {
        filmRepository.getNotifications()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { notes ->
                notificationsLiveData.postValue(notes)
            }?.let { compositeDisposable.add(it) }
    }

    fun sendNotification(
        context: Context,
        filmId: Int, time: Long,
        sendNow: Boolean
    ) {
        filmRepository.getFilmById(filmId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it ->
                it?.let { film ->
                    Log.d(TAG, "sendFilmNotification: film id = ${film.id}")
                    FilmNotificationPublisher.sendFilmNotification(
                        context, film, time, sendNow
                    )
                }
            }?.let { compositeDisposable.add(it) }
    }

    fun addNotification(
        notification: Notification, context: Context, time: Long,
        sendNow: Boolean
    ) {
        compositeDisposable.add(
            filmRepository.addNotification(notification)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d(TAG, "sendNotification: id = ${notification.id}")
                        sendNotification(
                            context, notification.id, time, sendNow
                        )
                        getNotifications()
                    },
                    { throwable ->
                        Log.d(TAG, "Add notification error: ${throwable.message}")
                    }
                )
        )
    }

    fun deleteNotification(id: Int) {
        compositeDisposable.add(
            filmRepository.deleteNotification(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        getNotifications()
                    },
                    { throwable ->
                        Log.d(TAG, "Delete notification error: ${throwable.message}")
                    }
                )
        )
    }

    fun addToFavorite(id: Int) {
        filmRepository.addToFavorite(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                if (result > 0) {
                    updateFavorites()
                }
            }?.let { compositeDisposable.add(it) }
    }

    fun updateFilm(film: Film) {
        filmRepository.updateFilm(film)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                if (result > 0) {
                    updateFilms()
                    updateFavorites()
                }
            }?.let { compositeDisposable.add(it) }
    }

    fun getFilmById(id: Int) {
        isSelectedLiveData.postValue(true)
        filmRepository.getFilmById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { film ->
                film?.let {
                    selectedFilmData.postValue(it)
                    isSelectedLiveData.postValue(false)
                }
            }?.let { compositeDisposable.add(it) }
    }

    fun clearFilms() {
        page = 0
        compositeDisposable.add(
            filmRepository.clearFilms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        updateFilms()
                        updateFavorites()
                    },
                    { throwable ->
                        Log.d(TAG, "Delete cash error: ${throwable.message}")
                    }
                )
        )
    }
}
