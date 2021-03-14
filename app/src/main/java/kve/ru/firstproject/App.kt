package kve.ru.firstproject

import android.app.Application
import kve.ru.firstproject.utils.NetworkUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    lateinit var api: Api

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        initRetrofit()
    }

    private fun initRetrofit() {
        val client = OkHttpClient.Builder()
//            .addInterceptor { chain ->
//                return@addInterceptor chain.proceed(
//                    chain
//                        .request()
//                        .newBuilder()
//                        .addHeader("Authorization", "Bearer fdkghgegnin")
//                        .build()
//                )
//            }
            .addInterceptor(
                HttpLoggingInterceptor()
                .apply {
                    if (BuildConfig.DEBUG) {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
                })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(NetworkUtils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(Api::class.java)
    }
}