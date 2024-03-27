package project.social.whisper

import adapters.DatabaseAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import project.social.whisper.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : BaseActivity() {
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
                sendPasswordResetEmail(b.edtRstEmail.text.toString())
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {

        DatabaseAdapter.usersTable.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(s in snapshot.children)
                    {
                        val email2 = s.child("EMAIL").getValue(String::class.java)!!

                        if(email2 == email)
                        {
                            sending(email)
                            return
                        }
                    }
                    Toast.makeText(applicationContext, "Email ID does not exist, Register first"
                        , Toast.LENGTH_LONG).show()
                }
                else
                {
                    Toast.makeText(applicationContext, "Email ID does not exist, Register first"
                        , Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun sending(email: String) {
        DatabaseAdapter.passwordResetMail(email){isValid ->
            when (isValid) {
                "true" -> {
                    Toast.makeText(this@ForgotPasswordActivity,
                        "Password reset link is send to your email address",
                        Toast.LENGTH_LONG).show()

                    val i = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
                    startActivity(i)
                }
                "exist" -> {
                    Toast.makeText(this@ForgotPasswordActivity,
                        "Email address does not exist, First register with this email address",
                        Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toast.makeText(this@ForgotPasswordActivity,
                        "Something went wrong, Try again",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun getSelectedTheme(): String {
        val sharedPreferences = getSharedPreferences("app_theme", MODE_PRIVATE)
        return sharedPreferences.getString("theme", "primary1")?: "primary1"
    }

    override fun getWhiteOrBlackTheme(): String {
        val sharedPreferences = getSharedPreferences("app_theme_wb", MODE_PRIVATE)
        return sharedPreferences.getString("theme_wb", "system")?: "system"
    }
}
