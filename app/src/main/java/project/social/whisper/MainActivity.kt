package project.social.whisper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import project.social.whisper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View Binding
        val b = ActivityMainBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        b.btnLogin.setOnClickListener {
            val login = Intent(applicationContext, LoginActivity::class.java)
            startActivity(login)
        }

        b.btnSignup.setOnClickListener {
            val signup = Intent(applicationContext, RegistrationActivity::class.java)
            startActivity(signup)
        }

    }
}