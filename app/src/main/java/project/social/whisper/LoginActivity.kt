package project.social.whisper

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import project.social.whisper.databinding.ActivityLoginBinding
import services.NotificationService

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var gso:GoogleSignInOptions
    private lateinit var gClient: GoogleSignInClient
    private val RC_SIGN_IN = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        View binding
        val b = ActivityLoginBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        //Google
        b.btnLogGoogle.setOnClickListener {
            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            gClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = gClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

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
            val mail = b.edtLogEmail.text.trim().toString()
            val pass = b.edtLogPassword.text.trim().toString()

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
                    checkIfUsernameExist()
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

    private fun checkIfUsernameExist() {

        GlobalStaticAdapter.uid = DatabaseAdapter.returnUser()?.uid ?: "None"

        if(GlobalStaticAdapter.uid != "None") {
            DatabaseAdapter.userDetailsTable.child(GlobalStaticAdapter.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.exists()) {
                            for(s in snapshot.children)
                            {
                                if(s.exists())
                                {

                                    GlobalStaticAdapter.key = s.key!!

                                    GlobalStaticAdapter.userName = s.child("USER_NAME")
                                        .getValue(String::class.java)!!

                                    GlobalStaticAdapter.about = s.child("ABOUT")
                                        .getValue(String::class.java)!!

                                    GlobalStaticAdapter.accountType = s.child("ACCOUNT_TYPE")
                                        .getValue(String::class.java)!!

                                    GlobalStaticAdapter.imageUrl = s.child("IMAGE")
                                        .getValue(String::class.java)!!

                                    //FCM Token
                                    NotificationService.generateToken()

                                    val mainActivity = Intent(applicationContext, MainActivity::class.java)
                                    startActivity(mainActivity)
                                    break
                                }
                                else
                                {
                                    val mainActivity = Intent(applicationContext, AddDetailsActivity::class.java)
                                    startActivity(mainActivity)
                                }
                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { t ->
                            if (t.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("DB_ERROR", "signInWithCredential:success")
                                if(DatabaseAdapter.returnUser()!=null)
                                {
                                    val uid = DatabaseAdapter.returnUser()?.uid!!

                                    GlobalStaticAdapter.uid = uid

                                    DatabaseAdapter.usersTable.child(uid).child("EMAIL")
                                        .setValue(DatabaseAdapter.returnUser()?.email?.lowercase())

                                    DatabaseAdapter.usersTable.child(uid)
                                        .child("EMAIL_VERIFIED").setValue(true)

                                    checkEmailExistWithGoogle(DatabaseAdapter.returnUser()?.email?.lowercase())
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("DB_ERROR", "signInWithCredential:failure", t.exception)
                                Toast.makeText(this, "Something went wrong",Toast.LENGTH_LONG).show()
                            }
                        }
                }
            } catch (e: ApiException) {
                // Handle exception
                Log.d("DB_ERROR", e.toString())
                Toast.makeText(this, "Something went wrong",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkEmailExistWithGoogle(userEmail:String?) {
        DatabaseAdapter.usersTable.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(s in snapshot.children)
                    {
                        if(s.exists())
                        {
                            val email = s.child("EMAIL").getValue(String::class.java)?:"none"

                            if(email == userEmail)
                            {
                                collectValues()
                                return
                            }
                        }
                    }
                    //Move to diff Activity
                    val i = Intent(applicationContext, AddDetailsActivity::class.java)
                    startActivity(i)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun collectValues() {

        DatabaseAdapter.userDetailsTable.child(GlobalStaticAdapter.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        for(s in snapshot.children)
                        {
                            if(s.exists())
                            {

                                GlobalStaticAdapter.key = s.key!!

                                GlobalStaticAdapter.userName = s.child("USER_NAME")
                                    .getValue(String::class.java)!!

                                GlobalStaticAdapter.about = s.child("ABOUT")
                                    .getValue(String::class.java)!!

                                GlobalStaticAdapter.accountType = s.child("ACCOUNT_TYPE")
                                    .getValue(String::class.java)!!

                                GlobalStaticAdapter.imageUrl = s.child("IMAGE")
                                    .getValue(String::class.java)!!

                                //FCM Token
                                NotificationService.generateToken()

                                //Move to diff Activity
                                val i = Intent(applicationContext, MainActivity::class.java)
                                startActivity(i)
                                break
                            }
                            else
                            {
                                val mainActivity = Intent(applicationContext, AddDetailsActivity::class.java)
                                startActivity(mainActivity)
                            }
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}