package project.social.whisper

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import project.social.whisper.databinding.ActivityAddDetailsBinding
import services.NotificationService

class AddDetailsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View binding
        val b = ActivityAddDetailsBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        b.txtDetIsAvailable.visibility = View.GONE

        b.edtDetUserName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Not needed for this example
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check for username availability when the user is typing
                val username = s.toString()

                if (username.trim().isNotEmpty()) {
                    checkUsernameAvailability(username, b.txtDetIsAvailable)
                } else {
                    b.txtDetIsAvailable.visibility = View.GONE
                }
            }
        })

        b.btnDetCreateAcc.setOnClickListener {
            if(b.txtDetIsAvailable.text.toString() == "User name is already exist"
                || b.edtDetUserName.text.trim().toString() == ""
                || b.edtDetUserName.text.toString().contains(" "))
            {
                Toast.makeText(this, "Please enter unique user name and should not contain space", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else
            {
                if(GlobalStaticAdapter.key != "")
                {
                    DatabaseAdapter.userDetailsTable.child(GlobalStaticAdapter.uid)
                        .child(GlobalStaticAdapter.key)
                        .child("IS_OPENED").setValue(false)
                    Log.d("LOGIN_ERROR", "Hi0")
                }

                val user = DatabaseAdapter.returnUser()!!
                if(user.isEmailVerified || user.phoneNumber != null) {

                    try {

                        Log.d("LOGIN_ERROR", "Hi1")

                        GlobalStaticAdapter.key =
                            DatabaseAdapter.userDetailsTable.push().key!!

                        GlobalStaticAdapter.about = ""

                        NotificationService.generateToken()
                        Log.d("LOGIN_ERROR", "Hi2")

                        DatabaseAdapter.userDetailsTable.child(GlobalStaticAdapter.uid)
                            .child(GlobalStaticAdapter.key)
                            .child("IS_OPENED")
                            .setValue(true)

                        Log.d("DB_ERROR", GlobalStaticAdapter.key)

                        DatabaseAdapter.userDetailsTable.child(GlobalStaticAdapter.uid)
                            .child(GlobalStaticAdapter.key)
                            .child("USER_NAME")
                            .setValue(b.edtDetUserName.text.toString().lowercase())

                        Log.d("LOGIN_ERROR", "Hi3")
                    }catch(e:Exception)
                    {
                        Log.d("DB_ERROR",e.toString())
                    }

                    GlobalStaticAdapter.userName = b.edtDetUserName.text.toString().lowercase()
                    GlobalStaticAdapter.imageUrl = getString(R.string.image_not_found)

                    Log.d("LOGIN_ERROR", "Hi4")

                    DatabaseAdapter.usersTable.child(GlobalStaticAdapter.uid).child("EMAIL_VERIFIED").setValue(true)
                    val mainAct = Intent(this, MainActivity::class.java)
                    startActivity(mainAct)

                    Log.d("LOGIN_ERROR", "Hi5")
                }
                else
                {
                    Toast.makeText(applicationContext, "First verify email to continue", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun checkUsernameAvailability(username: String, availabilityTextView: TextView) {
        // Check if the username exists in the database
        DatabaseAdapter.userDetailsTable.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (s in snapshot.children) {
                        Log.d("DB_ERROR", s.key!!)
                        if (s.exists()) {
                            for (userSnapshot in s.children) {
                                Log.d("DB_ERROR", userSnapshot.key!!)
                                if (userSnapshot.exists()) {
                                    val dbUserName =
                                        userSnapshot.child("USER_NAME").getValue(String::class.java)

                                    if (dbUserName != null && dbUserName.equals(
                                            username,
                                            ignoreCase = true
                                        )
                                    ) {
                                        availabilityTextView.text = "User name is already exist"
                                        val drawableStart: Drawable? = ContextCompat.getDrawable(
                                            applicationContext,
                                            R.drawable.cross
                                        )
                                        val textColor = Color.parseColor("#FF0000")
                                        availabilityTextView.setTextColor(textColor)
                                        availabilityTextView.setCompoundDrawablesWithIntrinsicBounds(
                                            drawableStart,
                                            null,
                                            null,
                                            null
                                        )
                                        availabilityTextView.visibility = View.VISIBLE
                                        return
                                    } else {
                                        availabilityTextView.text = "User name is available"
                                        val drawableStart: Drawable? = ContextCompat.getDrawable(
                                            applicationContext,
                                            R.drawable.tick
                                        )
                                        val textColor = Color.parseColor("#6B9738")
                                        availabilityTextView.setTextColor(textColor)
                                        availabilityTextView.setCompoundDrawablesWithIntrinsicBounds(
                                            drawableStart,
                                            null,
                                            null,
                                            null
                                        )
                                        availabilityTextView.visibility = View.VISIBLE
                                    }
                                }
                            }
                        }
                    }
                }
                else
                {
                    availabilityTextView.text = "User name is available"
                    val drawableStart: Drawable? = ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.tick
                    )
                    val textColor = Color.parseColor("#6B9738")
                    availabilityTextView.setTextColor(textColor)
                    availabilityTextView.setCompoundDrawablesWithIntrinsicBounds(
                        drawableStart,
                        null,
                        null,
                        null
                    )
                    availabilityTextView.visibility = View.VISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                availabilityTextView.text = "Error checking username availability"
                availabilityTextView.visibility = View.VISIBLE
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