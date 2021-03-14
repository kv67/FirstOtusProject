package kve.ru.firstproject.utils

class NetworkUtils {
    companion object {
        const val TAG = "NetworkUtils"
        const val BASE_URL = "https://api.themoviedb.org/3/"

        const val SORT_BY_POPULARITY = "popularity.desc"
        const val SORT_BY_TOP_RATED = "vote_average.desc"
        const val API_KEY = "1b097d2d522fb2f73b520ed253664d65"
        const val POPULARITY = 0
        const val TOP_RATED = 1
        const val BASE_POSTER_URL = "https://image.tmdb.org/t/p/"
        const val SMALL_POSTER_SIZE = "w185"
        const val BIG_POSTER_SIZE = "w780"
    }

}