package kve.ru.firstproject.repositories

import kve.ru.firstproject.App
import kve.ru.firstproject.utils.NetworkUtils
import java.util.*

class ApiRepository {

    fun loadData(page: Int) = App.instance.api.getMovies(
        NetworkUtils.API_KEY, Locale.getDefault().language,
        NetworkUtils.SORT_BY_POPULARITY, (page + 1).toString()
    )

}