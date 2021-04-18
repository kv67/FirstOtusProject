package kve.ru.firstproject.repositories

import kve.ru.firstproject.App
import kve.ru.firstproject.pojo.BestMovie
import kve.ru.firstproject.pojo.MovieResponse
import kve.ru.firstproject.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ApiRepository {

    fun loadData(
        page: Int,
        filmsListener: ((films: List<BestMovie?>?) -> Unit),
        errorListener: ((msg: String?) -> Unit)
    ) {
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
                        filmsListener.invoke(this.movies)
                    } ?: run {
                        filmsListener.invoke(null)
                    }
                } else {
                    errorListener.invoke(response.message())
                }
            }

            override fun onFailure(call: Call<MovieResponse?>, t: Throwable) {
                errorListener.invoke(t.message)
            }
        })
    }
}