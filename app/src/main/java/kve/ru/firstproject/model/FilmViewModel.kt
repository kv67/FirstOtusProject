package kve.ru.firstproject.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kve.ru.firstproject.MainActivity
import kve.ru.firstproject.db.Film
import kve.ru.firstproject.db.Notification
import kve.ru.firstproject.repositories.ApiRepository
import kve.ru.firstproject.repositories.FilmRepository

class FilmViewModel : ViewModel() {

    private val filmsLiveData = MutableLiveData<List<Film>>()
    private val favoritesLiveData = MutableLiveData<List<Film>>()
    private val notificationsLiveData = MutableLiveData<List<Notification>>()
    private val selectedFilmData = MutableLiveData<Film>()
    private val isSelectedLiveData = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<String>()
    private val loadingLiveData = MutableLiveData<Boolean>()
    private val favoriteUpdatedLiveData = MutableLiveData<Int>()
    private val filmRepository: FilmRepository = FilmRepository()

    init {
        updateFilm(null)
        loadingLiveData.postValue(false)
        favoriteUpdatedLiveData.postValue(0)
    }

    companion object {
        var page = 0
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

    fun clearErrors() {
        errorLiveData.postValue(null)
    }

    fun refreshData() {
        page = 0
        loadData()
    }

    fun loadData() {
        loadingLiveData.postValue(true)
        apiRepository.loadData(page,
            { films ->
                films?.let {
                    filmRepository.saveFilmsToDb(it) { films ->
                        filmsLiveData.postValue(films)
                    }
                    page++
                }
                loadingLiveData.postValue(false)
            },
            { error ->
                errorLiveData.postValue(error)
                loadingLiveData.postValue(false)
            }
        )
    }

    fun getNotifications() {
        filmRepository.getNotifications {
            notificationsLiveData.postValue(it)
        }
    }

    fun addNotification(notification: Notification) {
        filmRepository.addNotification(notification) {
            notificationsLiveData.postValue(it)
        }
    }

    fun deleteNotification(id: Int) {
        filmRepository.deleteNotification(id) {
            notificationsLiveData.postValue(it)
        }
    }

    fun addToFavorite(id: Int) {
        filmRepository.addToFavorite(id) {
            favoritesLiveData.postValue(it)
        }
    }

    fun updateFilm(film: Film?) {
        filmRepository.updateFilm(film,
            { films -> filmsLiveData.postValue(films) },
            { favorites -> favoritesLiveData.postValue(favorites) }
        )
    }

    fun getFilmById(id: Int) {
        isSelectedLiveData.postValue(true)
        Log.d(MainActivity.TAG, "Model getFilmById: id = $id, isSelected = ${isSelected.value}")
        filmRepository.getFilmById(id) {
            selectedFilmData.postValue(it)
            isSelectedLiveData.postValue(false)
        }
    }

    fun clearFilms() {
        page = 0
        filmRepository.clearFilms(
            { films -> filmsLiveData.postValue(films) },
            { favorites -> favoritesLiveData.postValue(favorites) }
        )
    }
}