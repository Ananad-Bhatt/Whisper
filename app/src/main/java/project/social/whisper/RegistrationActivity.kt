package project.social.whisper

import adapters.DatabaseAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import project.social.whisper.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private var usersTable = Firebase.database.getReference("USERS")

    //Google
    private lateinit var auth: FirebaseAuth
    private lateinit var gso:GoogleSignInOptions
    private lateinit var gClient:GoogleSignInClient
    private var RC_SIGN_IN = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View binding
        val b = ActivityRegistrationBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        b.btnRegLogin.setOnClickListener {
            val login = Intent(this, LoginActivity::class.java)
            startActivity(login)
        }

        //Google
        b.btnRegGoogle.setOnClickListener {
            //Google sign in
//            signInRequest = BeginSignInRequest.builder()
//                .setGoogleIdTokenRequestOptions(
//                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        // Your server's client ID, not your Android client ID.
//                        .setServerClientId(getString(R.string.default_web_client_id))
//                        // Only show accounts previously used to sign in.
//                        .setFilterByAuthorizedAccounts(true)
//                        .build())
//                .build()

            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            gClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = gClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

        }

        //Verify email
        b.btnRegVerify.setOnClickListener {
            if(b.edtRegEmail.text.toString().isEmpty())
            {
                Toast.makeText(this,"Email cannot be empty!", Toast.LENGTH_LONG).show()
                b.edtRegEmail.error = "Enter email"
                b.edtRegEmail.requestFocus()
                return@setOnClickListener
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(b.edtRegEmail.text.toString()).matches()) {
                Toast.makeText(this, "Enter valid email address", Toast.LENGTH_LONG).show()
                b.edtRegEmail.error = "Enter valid email"
                b.edtRegEmail.requestFocus()
                return@setOnClickListener
            }

            if(b.edtRegPassword.text.toString().isEmpty())
            {
                Toast.makeText(this,"Password cannot be empty!", Toast.LENGTH_LONG).show()
                b.edtRegPassword.error = "Enter password"
                b.edtRegPassword.requestFocus()
                return@setOnClickListener
            }

            if(b.edtRegConPassword.text.toString().isEmpty())
            {
                Toast.makeText(this,"Confirm your password", Toast.LENGTH_LONG).show()
                b.edtRegConPassword.error = "Confirm password"
                b.edtRegConPassword.requestFocus()
                return@setOnClickListener
            }

            if(b.edtRegPassword.text.toString().length < 6)
            {
                Toast.makeText(this,"Password length should be more than 6 letters", Toast.LENGTH_LONG).show()
                b.edtRegPassword.error = "Too weak"
                b.edtRegPassword.requestFocus()
                return@setOnClickListener
            }

            if(b.edtRegPassword.text.toString() != b.edtRegConPassword.text.toString())
            {
                Toast.makeText(this,"Confirm password is different", Toast.LENGTH_LONG).show()
                b.edtRegConPassword.error = "Password does not match"
                b.edtRegConPassword.requestFocus()
                return@setOnClickListener
            }


            //If everything perfect
            DatabaseAdapter.signUpWithMail(b.edtRegEmail.text.toString(), b.edtRegPassword.text.toString()) {
                when (it) {
                    "true" -> {
                        Toast.makeText(this,"Done : ${DatabaseAdapter.returnUser()?.email}",
                            Toast.LENGTH_LONG)
                            .show()

                        DatabaseAdapter.verifyEmail { it1 ->
                            if(it1) {
                                Toast.makeText(this,"Email verification link is sent to your email, Please verify your email",
                                    Toast.LENGTH_LONG).show()
                            }
                            else
                            {
                                Toast.makeText(this,"We are unable to send you verification mail",
                                    Toast.LENGTH_LONG).show()
                            }

                            //Store user details in Real Time Database
                            val key = DatabaseAdapter.returnUser()?.uid

                            if (key != null) {
                                try {
                                    usersTable.child(key).child("EMAIL")
                                        .setValue(DatabaseAdapter.returnUser()?.email?.lowercase())

                                    usersTable.child(key).child("EMAIL_VERIFIED")
                                        .setValue("false")

                                }catch(e:Exception)
                                {
                                    Log.d("DB_ERROR",e.toString())
                                }
                            }
                            else
                            {
                                return@verifyEmail
                            }

                            //Move to diff activity
                            val i = Intent(this, MainActivity::class.java)
                            startActivity(i)
                        }
                    }
                    "exist" -> {
                        Toast.makeText(this,"Email ID is already exist", Toast.LENGTH_LONG).show()
                        return@signUpWithMail
                    }
                    else -> {
                        Toast.makeText(this,"Something went wrong", Toast.LENGTH_LONG).show()
                        return@signUpWithMail
                    }
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

                                    usersTable.child(key).child("EMAIL")
                                        .setValue(user.email?.lowercase())

                                    usersTable.child(key).child("EMAIL_VERIFIED").setValue("true")

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
            Toast.makeText(this, "Welcome back ${currentUser.displayName}",Toast.LENGTH_LONG).show()
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }


}
