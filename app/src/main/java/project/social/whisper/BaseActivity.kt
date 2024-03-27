package project.social.whisper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set the theme before calling super.onCreate
        setAppTheme()

        super.onCreate(savedInstanceState)
    }

    private fun setAppTheme() {
        // Get the user's selected theme from shared preferences or some other storage

        // Set the theme based on the user's selection
        when (getSelectedTheme()) {
            "primary1" -> setTheme(R.style.Base_Theme_Whisper)
            "primary2" -> setTheme(R.style.Theme_red)
            "primary3" -> setTheme(R.style.Theme_green)
            "primary4" -> setTheme(R.style.Theme_navy_blue)
            "primary5" -> setTheme(R.style.Theme_orange)
            "primary6" -> setTheme(R.style.Theme_yellow)
        }

        when(getWhiteOrBlackTheme())
        {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

    }

    abstract fun getSelectedTheme(): String

    abstract fun getWhiteOrBlackTheme(): String
}
