package kve.ru.firstproject

import kve.ru.firstproject.pojo.MovieResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("discover/movie")
    fun getMovies(
        @Query("api_key") apiKey: String?,
        @Query("language") lang: String?, @Query("sort_by") sortMethod: String?,
        @Query("page") page: String?
    ): Call<MovieResponse?>?
}