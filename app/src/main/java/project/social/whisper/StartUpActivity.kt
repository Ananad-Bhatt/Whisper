package project.social.whisper

import adapters.DatabaseAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import project.social.whisper.databinding.ActivityStartUpBinding

class StartUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View Binding
        val b = ActivityStartUpBinding.inflate(layoutInflater)
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