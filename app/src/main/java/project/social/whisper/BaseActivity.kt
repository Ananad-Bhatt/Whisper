package project.social.whisper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

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
            "red" -> setTheme(R.style.Theme_red)
            "purple" -> setTheme(R.style.Base_Theme_Whisper)
            "green" -> setTheme(R.style.Theme_green)
            "navy_blue" -> setTheme(R.style.Theme_navy_blue)
            "orange" -> setTheme(R.style.Theme_orange)
            "yellow" -> setTheme(R.style.Theme_yellow)
        }
    }

    abstract fun getSelectedTheme(): String
}
