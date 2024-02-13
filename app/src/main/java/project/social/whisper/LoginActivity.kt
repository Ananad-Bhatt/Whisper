package project.social.whisper

import adapters.DatabaseAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import project.social.whisper.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var gso:GoogleSignInOptions
    private lateinit var gClient: GoogleSignInClient
    private var RC_SIGN_IN = 123

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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)

                // You can get the user's email and other details using account.getEmail(), account.getDisplayName(), etc.
                // Now you can authenticate with Firebase
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { t ->
                            if (t.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("DB_ERROR", "signInWithCredential:success")
                                val user = auth.currentUser
                                if(user!=null)
                                {
                                    val key = user.uid

                                    DatabaseAdapter.usersTable.child(key).child("EMAIL")
                                        .setValue(user.email?.lowercase())

                                    DatabaseAdapter.usersTable.child(key).child("EMAIL_VERIFIED").setValue("true")

                                    //Move to diff Activity
                                    val i = Intent(this, MainActivity::class.java)
                                    startActivity(i)
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

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null)
        {
            //Move to diff Activity
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }
}