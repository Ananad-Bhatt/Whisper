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

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = DatabaseAdapter.returnUser()
        if(currentUser != null)
        {
            //Find user name
            val uid = DatabaseAdapter.returnUser()?.uid!!

            DatabaseAdapter.userDetailsTable.child(uid).addListenerForSingleValueEvent(object:
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())
                    {
                        for(s in snapshot.children)
                        {
                            val key = s.key!!
                            val isOpened = s.child("IS_OPENED").getValue(Boolean::class.java) ?: true
                            if(isOpened)
                            {
                                DatabaseAdapter.key = key

                                //Move to diff Activity
                                val i = Intent(applicationContext, MainActivity::class.java)
                                startActivity(i)

                                return
                            }
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
}