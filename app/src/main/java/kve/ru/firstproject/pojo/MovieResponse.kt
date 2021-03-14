package kve.ru.firstproject.pojo

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("results")
    var movies: List<BestMovie?>? = null
)