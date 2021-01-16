package kve.ru.firstproject

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    companion object {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}