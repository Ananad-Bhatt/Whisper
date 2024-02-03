package project.social.whisper

import adapters.DatabaseAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.Toast
import project.social.whisper.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        b.btnRstReset.setOnClickListener {
            if(b.edtRstEmail.text.toString() == "")
            {
                Toast.makeText(this@ForgotPasswordActivity,"Enter Email id",Toast.LENGTH_LONG)
                    .show()
            }
            else
            {
                DatabaseAdapter.passwordResetMail(b.edtRstEmail.text.toString()){isValid ->
                    if(isValid == "true")
                    {
                        Toast.makeText(this@ForgotPasswordActivity,
                            "Password reset link is send to your email address",
                            Toast.LENGTH_LONG).show()

                        val i = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
                        startActivity(i)
                    }
                    else if(isValid == "exist")
                    {
                        Toast.makeText(this@ForgotPasswordActivity,
                            "Email address is not exist, First register with this email address",
                            Toast.LENGTH_LONG).show()
                    }
                    else
                    {
                        Toast.makeText(this@ForgotPasswordActivity,
                            "Something went wrong, Try again",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
