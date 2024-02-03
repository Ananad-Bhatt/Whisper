package project.social.whisper

import adapters.DatabaseAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.Toast
import project.social.whisper.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        View binding
        val b = ActivityLoginBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        b.btnLogSignup.setOnClickListener {
            val signUpActivity =
                Intent(applicationContext, RegistrationActivity::class.java)
            startActivity(signUpActivity)
        }

        b.txtLogResetPass.setOnClickListener {
            val i = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(i)
        }

        b.btnLogLogin.setOnClickListener {
            val mail = b.edtLogEmail.text.toString()
            val pass = b.edtLogPassword.text.toString()

            if(mail == "")
            {
                b.edtLogEmail.error = "Enter email id"
                b.edtLogPassword.requestFocus()
                return@setOnClickListener
            }
            if(pass == "")
            {
                b.edtLogPassword.error = "Enter password"
                b.edtLogPassword.requestFocus()
                return@setOnClickListener
            }

            DatabaseAdapter.signInWithMail(mail, pass) { isLogin ->
                if (isLogin) {
                    val mainActivity = Intent(applicationContext, MainActivity::class.java)
                    startActivity(mainActivity)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Email or password is wrong",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }
    }
}