package project.social.whisper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import project.social.whisper.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Binding View
        val b = ActivitySplashBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        //Creating animation variable
        val slideLeftIn: Animation = AnimationUtils.loadAnimation(this, R.anim.slide_left_in)
        val largeToSmall: Animation = AnimationUtils.loadAnimation(this, R.anim.large_to_small)

        //Setting animation to views
        b.splashLogo.startAnimation(slideLeftIn)
        b.appName.startAnimation(largeToSmall)

        //Creating delay for splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            val login = Intent(this, StartUpActivity::class.java)
            startActivity(login)
            finish()
        }, 4000)

    }
}