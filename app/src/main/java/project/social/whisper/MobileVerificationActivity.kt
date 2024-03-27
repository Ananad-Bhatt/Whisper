package project.social.whisper

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import project.social.whisper.databinding.ActivityMobileVerificationBinding
import services.NotificationService
import java.util.concurrent.TimeUnit

class MobileVerificationActivity : BaseActivity() {

    private lateinit var b:ActivityMobileVerificationBinding

    private val auth = FirebaseAuth.getInstance()

    private var OTP  = ""

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d("PHONE_AUTH", "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("PHONE_AUTH", "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            OTP = verificationId
            Log.d("PHONE_AUTH", "onCodeSent:$verificationId")

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityMobileVerificationBinding.inflate(layoutInflater)
        setContentView(b.root)

        addTextChangeListener()

        b.btnLoginMobile.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        b.btnSignUpMobile.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        b.btnOtpVerifyMobile.setOnClickListener {
            val typedOTP =
                (b.edtOtp1.text.toString() + b.edtOtp2.text.toString() + b.edtOtp3.text.toString()
                        + b.edtOtp4.text.toString() + b.edtOtp5.text.toString() + b.edtOtp6.text.toString())

            if(typedOTP.isNotEmpty() && typedOTP.length == 6)
            {
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    OTP, typedOTP
                )
                signInWithPhoneAuthCredential(credential)
            }
            else
                Toast.makeText(this, "Entered OTP is wrong", Toast.LENGTH_LONG).show()
        }

        b.btnSendOtpMobile.setOnClickListener {
            if (b.edtMobileVerification.text.trim().length == 10 &&
                !b.edtMobileVerification.text.toString().contains(" "))
            {
                val number = "+91${b.edtMobileVerification.text}"

                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(number) // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this) // Activity (for callback binding)
                    .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)

                b.llMobileSendOtp.visibility = View.GONE
                b.llVerifyOtp.visibility = View.VISIBLE
            }
            else
                Toast.makeText(this, "Enter valid mobile number", Toast.LENGTH_LONG).show()
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("PHONE_AUTH", "signInWithCredential:success")

                    val user = task.result?.user

                    val uid = DatabaseAdapter.returnUser()?.uid!!
                    val key = DatabaseAdapter.userDetailsTable.child(uid).push().key!!

                    GlobalStaticAdapter.uid = uid

                    DatabaseAdapter.usersTable.child(uid).child("EMAIL")
                        .setValue(DatabaseAdapter.returnUser()?.phoneNumber?.lowercase())

                    DatabaseAdapter.usersTable.child(uid)
                        .child("EMAIL_VERIFIED").setValue(true)

                    checkMobileExist(DatabaseAdapter.returnUser()?.phoneNumber?.lowercase())

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("PHONE_AUTH", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
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
                                    val isOpened = s.child("IS_OPENED")
                                        .getValue(Boolean::class.java) ?: true

                                    if(isOpened) {

                                        GlobalStaticAdapter.key = s.key!!

                                        GlobalStaticAdapter.userName = s.child("USER_NAME")
                                            .getValue(String::class.java)!!

                                        GlobalStaticAdapter.about = s.child("ABOUT")
                                            .getValue(String::class.java) ?: ""

                                        GlobalStaticAdapter.accountType = s.child("ACCOUNT_TYPE")
                                            .getValue(String::class.java) ?: "PUBLIC"

                                        GlobalStaticAdapter.imageUrl = s.child("IMAGE")
                                            .getValue(String::class.java)
                                            ?: getString(R.string.image_not_found)

                                        //FCM Token
                                        NotificationService.generateToken()

                                        val mainActivity =
                                            Intent(applicationContext, MainActivity::class.java)
                                        startActivity(mainActivity)
                                        return
                                    }
                                }
                                else
                                {
                                    val mainActivity = Intent(applicationContext, AddDetailsActivity::class.java)
                                    startActivity(mainActivity)
                                }
                            }
                        }
                        else{
                            val mainActivity = Intent(applicationContext, AddDetailsActivity::class.java)
                            startActivity(mainActivity)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
        else
        {
            Toast.makeText(applicationContext, "Something went wrong, please Try again!!!", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkMobileExist(userEmail:String?) {
        DatabaseAdapter.usersTable.addListenerForSingleValueEvent(object: ValueEventListener {
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
                                checkIfUsernameExist()
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

    private fun addTextChangeListener() {
        b.edtOtp1.addTextChangedListener(EditTextWatcher(b.edtOtp1))
        b.edtOtp2.addTextChangedListener(EditTextWatcher(b.edtOtp2))
        b.edtOtp3.addTextChangedListener(EditTextWatcher(b.edtOtp3))
        b.edtOtp4.addTextChangedListener(EditTextWatcher(b.edtOtp4))
        b.edtOtp5.addTextChangedListener(EditTextWatcher(b.edtOtp5))
        b.edtOtp6.addTextChangedListener(EditTextWatcher(b.edtOtp6))
    }

    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }
        override fun afterTextChanged(p0: Editable?) {

            val text = p0.toString()
            when (view.id) {
                R.id.edt_otp_1 -> if (text.length == 1) b.edtOtp2.requestFocus()
                R.id.edt_otp_2 -> if (text.length == 1) b.edtOtp3.requestFocus() else if (text.isEmpty()) b.edtOtp1.requestFocus()
                R.id.edt_otp_3 -> if (text.length == 1) b.edtOtp4.requestFocus() else if (text.isEmpty()) b.edtOtp2.requestFocus()
                R.id.edt_otp_4 -> if (text.length == 1) b.edtOtp5.requestFocus() else if (text.isEmpty()) b.edtOtp3.requestFocus()
                R.id.edt_otp_5 -> if (text.length == 1) b.edtOtp6.requestFocus() else if (text.isEmpty()) b.edtOtp4.requestFocus()
                R.id.edt_otp_6 -> if (text.isEmpty()) b.edtOtp5.requestFocus()
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