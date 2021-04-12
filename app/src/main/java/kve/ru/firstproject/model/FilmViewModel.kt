package kve.ru.firstproject.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kve.ru.firstproject.App
import kve.ru.firstproject.MainActivity
import kve.ru.firstproject.db.Db
import kve.ru.firstproject.db.Film
import kve.ru.firstproject.pojo.BestMovie
import kve.ru.firstproject.pojo.MovieResponse
import kve.ru.firstproject.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class FilmViewModel : ViewModel() {

    private val filmsLiveData = MutableLiveData<List<Film>>()
    private val favoritesLiveData = MutableLiveData<List<Film>>()
    private val selectedFilmData = MutableLiveData<Film>()
    private val errorLiveData = MutableLiveData<String>()
    private val loadingLiveData = MutableLiveData<Boolean>()
    private val favoriteUpdatedLiveData = MutableLiveData<Int>()

    init {
        updateFilm(null)
        loadingLiveData.postValue(false)
        favoriteUpdatedLiveData.postValue(0)
    }

    companion object {
        var page = 0
    }

    val films: LiveData<List<Film>>
        get() = filmsLiveData

    val favorites: LiveData<List<Film>>
        get() = favoritesLiveData

    val selectedFilm: LiveData<Film>
        get() = selectedFilmData

    val error: LiveData<String>
        get() = errorLiveData

    val loading: LiveData<Boolean>
        get() = loadingLiveData

    fun clearErrors() {
        errorLiveData.postValue(null)
    }

    private fun saveFilmsToDb(films: List<BestMovie?>) {
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
                filmsLiveData.postValue(it)
            }
        }
        Executors.newSingleThreadScheduledExecutor()
            .schedule(task, 20, TimeUnit.MILLISECONDS)
    }

    fun refreshData() {
        page = 0
        loadData()
    }

    fun loadData() {
        loadingLiveData.postValue(true)
        App.instance.api.getMovies(
            NetworkUtils.API_KEY, Locale.getDefault().language,
            NetworkUtils.SORT_BY_POPULARITY, (page + 1).toString()
        )?.enqueue(object : Callback<MovieResponse?> {
            override fun onResponse(
                call: Call<MovieResponse?>,
                response: Response<MovieResponse?>
            ) {
                if (response.isSuccessful) {
                    response.body()?.apply {
                        Log.d(MainActivity.TAG, "Films list size: ${this.movies?.size}")
                        this.movies?.let {
                            saveFilmsToDb(it)
                            page++
                        }
                    }
                } else {
                    errorLiveData.postValue(response.message())
                }
                loadingLiveData.postValue(false)
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                loadingLiveData.postValue(false)
                errorLiveData.postValue(t.message)
            }
        })
    }

    fun addToFavorite(id: Int) {
        val task = Runnable {
            Db.getInstance(App.instance)?.getFilmDao()?.addToFavorite(id)
            Db.getInstance(App.instance)?.getFilmDao()?.getFavorites()?.let {
                favoritesLiveData.postValue(it)
            }
        }
        Executors.newSingleThreadScheduledExecutor().schedule(task, 20, TimeUnit.MILLISECONDS)
    }

    fun updateFilm(film: Film?) {
        val task = Runnable {
            film?.let {
                Db.getInstance(App.instance)?.getFilmDao()?.updateFilm(it)
            }
            Db.getInstance(App.instance)?.getFilmDao()?.getAll()?.let {
                filmsLiveData.postValue(it)
            }
            Db.getInstance(App.instance)?.getFilmDao()?.getFavorites()?.let {
                favoritesLiveData.postValue(it)
            }
        }
        Executors.newSingleThreadScheduledExecutor().schedule(task, 20, TimeUnit.MILLISECONDS)
    }

    fun getFilmById(id: Int) {
        val task = Runnable {
            Db.getInstance(App.instance)?.getFilmDao()?.getById(id)?.let {
                selectedFilmData.postValue(it)
            }
        }
        Executors.newSingleThreadScheduledExecutor()
            .schedule(task, 20, TimeUnit.MILLISECONDS)
    }

    fun clearFilms() {
        page = 0
        val task = Runnable {
            Db.getInstance(App.instance)?.getFilmDao()?.deleteAll()
            Db.getInstance(App.instance)?.getFilmDao()?.getAll()?.let {
                filmsLiveData.postValue(it)
            }
            Db.getInstance(App.instance)?.getFilmDao()?.getFavorites()?.let {
                favoritesLiveData.postValue(it)
            }
        }
        Executors.newSingleThreadScheduledExecutor().schedule(task, 20, TimeUnit.MILLISECONDS)
    }
}