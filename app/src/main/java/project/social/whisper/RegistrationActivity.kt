package project.social.whisper

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
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
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import project.social.whisper.databinding.ActivityRegistrationBinding

class RegistrationActivity : BaseActivity() {

    //Google
    private lateinit var auth: FirebaseAuth
    private lateinit var gso:GoogleSignInOptions
    private lateinit var gClient:GoogleSignInClient
    private var RC_SIGN_IN = 123

    private lateinit var b:ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View binding
        b = ActivityRegistrationBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        b.btnRegLogin.setOnClickListener {
            val login = Intent(this, LoginActivity::class.java)
            startActivity(login)
        }

        //Google
        b.btnRegGoogle.setOnClickListener {

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
            if(b.edtRegEmail.text.trim().toString().isEmpty())
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

            if(b.edtRegPassword.text.trim().toString().isEmpty())
            {
                Toast.makeText(this,"Password cannot be empty!", Toast.LENGTH_LONG).show()
                b.edtRegPassword.error = "Enter password"
                b.edtRegPassword.requestFocus()
                return@setOnClickListener
            }

            if(b.edtRegConPassword.text.trim().toString().isEmpty())
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

            checkIfUserIsExistOrNot()
        }
    }

    private fun checkIfUserIsExistOrNot() {


        DatabaseAdapter.usersTable.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(s in snapshot.children)
                    {
                        if(s.exists())
                        {
                            val email = s.child("EMAIL").getValue(String::class.java)?:"none"

                            if(email == b.edtRegEmail.text.toString())
                            {
                                Toast.makeText(applicationContext, "Email ID is already exist, Please Login"
                                    , Toast.LENGTH_LONG).show()
                                return
                            }
                        }
                    }
                    signUpWithEmail()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun signUpWithEmail() {
        //If everything perfect
        DatabaseAdapter.signUpWithMail(b.edtRegEmail.text.toString(), b.edtRegPassword.text.toString()) {
            when (it) {
                "true" -> {
                    Toast.makeText(this,"Successfully created account as ${DatabaseAdapter.returnUser()?.email}",
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
                        val uid = DatabaseAdapter.returnUser()?.uid!!
                        val key = DatabaseAdapter.userDetailsTable.child(uid).push().key!!
                        GlobalStaticAdapter.uid = uid
                        GlobalStaticAdapter.key = key

                        try {
                            DatabaseAdapter.usersTable.child(uid).child("EMAIL")
                                .setValue(DatabaseAdapter.returnUser()?.email?.lowercase())

                            DatabaseAdapter.usersTable.child(uid).child("EMAIL_VERIFIED")
                                .setValue(false)

                        }catch(e:Exception)
                        {
                            Log.d("DB_ERROR",e.toString())
                        }

                        //Move to diff activity
                        val i = Intent(this, AddDetailsActivity::class.java)
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
                                if(DatabaseAdapter.returnUser()!=null)
                                {
                                    val uid = DatabaseAdapter.returnUser()?.uid!!
                                    val key = DatabaseAdapter.userDetailsTable.child(uid).push().key!!

                                    GlobalStaticAdapter.key = key
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
                                Toast.makeText(applicationContext, "Account is already exist, Please Login"
                                    , Toast.LENGTH_LONG).show()
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

    override fun getSelectedTheme(): String {
        val sharedPreferences = getSharedPreferences("app_theme", MODE_PRIVATE)
        return sharedPreferences.getString("theme", "primary1")?: "primary1"
    }

    override fun getWhiteOrBlackTheme(): String {
        val sharedPreferences = getSharedPreferences("app_theme_wb", MODE_PRIVATE)
        return sharedPreferences.getString("theme_wb", "system")?: "system"
    }
}
