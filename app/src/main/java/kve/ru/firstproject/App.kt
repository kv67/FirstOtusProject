package kve.ru.firstproject

import android.app.Application
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import kve.ru.firstproject.utils.FeatureToggles
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
        private const val TAG = "MainApp"
        var token: String? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initRetrofit()
        initRemoteConfig()
        getToken()
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result
            Log.d(TAG, "Current token: $token")
        })
    }

    private fun initRemoteConfig() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 60 else 3600
        }

        Firebase.remoteConfig.apply {
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(FeatureToggles.defaults)
            fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Config params updated: ${task.result}")
                } else {
                    Log.d(TAG, "Config params couldn't be updated")
                }
            }
        }
    }

    private fun initRetrofit() {
        val client = OkHttpClient.Builder()
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
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(Api::class.java)
    }
}