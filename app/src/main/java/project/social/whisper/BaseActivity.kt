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
            "primary1" -> setTheme(R.style.Base_Theme_Whisper)
            "primary2" -> setTheme(R.style.Theme_red)
            "primary3" -> setTheme(R.style.Theme_green)
            "primary4" -> setTheme(R.style.Theme_navy_blue)
            "primary5" -> setTheme(R.style.Theme_orange)
            "primary6" -> setTheme(R.style.Theme_yellow)
        }
    }

    abstract fun getSelectedTheme(): String
}
