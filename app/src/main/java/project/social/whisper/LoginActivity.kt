package project.social.whisper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import project.social.whisper.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = ActivityLoginBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        b.btnLogSignup.setOnClickListener {
            val signUpActivity = Intent(applicationContext, RegistrationActivity::class.java)
            startActivity(signUpActivity)
        }

        b.btnLogLogin.setOnClickListener {
            val mainActivity = Intent(applicationContext, MainActivity::class.java)
            startActivity(mainActivity)
        }
    }
}