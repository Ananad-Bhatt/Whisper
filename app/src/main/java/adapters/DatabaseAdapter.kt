package adapters

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class DatabaseAdapter {

    //Static methods and variables
    companion object{
        private lateinit var auth:FirebaseAuth

        //DB tables
        var usersTable = Firebase.database.getReference("USERS")
        var userDetailsTable = Firebase.database.getReference("USER_DETAILS")
        var chatTable = Firebase.database.getReference("CHATS")
        var chatRooms = Firebase.database.getReference("CHAT_ROOMS")

        //Storage
        var userImage = FirebaseStorage.getInstance().getReference("USER_IMAGES")

        fun returnUser():FirebaseUser?
        {
            auth = FirebaseAuth.getInstance()

            return auth.currentUser
        }

        fun verifyEmail(callback: (Boolean) -> Unit){

            auth = FirebaseAuth.getInstance()

            val user = auth.currentUser

            try {
                user?.sendEmailVerification()?.addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        callback(true)
                    }else
                    {
                        callback(false)
                        Log.d("DB_ERROR",it.toString())
                    }
                }
            }catch (e:Exception)
            {
                callback(false)
                Log.d("DB_ERROR",e.toString())
            }
        }

        fun signUpWithMail(mail:String, password:String, callback:(String) -> Unit)
        {
            auth = FirebaseAuth.getInstance()

            try {
                auth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        callback("true")
                    }
                    else{
                        if (it.exception is FirebaseAuthUserCollisionException) {
                            callback("exist") // Email already exists
                        } else {
                            Log.e("TAG", "Account creation failed: ${it.exception}")
                            callback("false") // Other error
                        }
                    }
                }.addOnFailureListener {
                    callback("false")
                    Log.d("DB_ERROR",it.toString())
                }
            }catch (e:Exception)
            {
                Log.d("DB_ERROR",e.toString())
            }
        }

        fun signInWithMail(mail:String, password:String, callback:(Boolean) -> Unit)
        {
            auth = FirebaseAuth.getInstance()

            try {
                auth.signInWithEmailAndPassword(mail, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                }.addOnFailureListener {
                    callback(false)
                }
            }catch(e:Exception)
            {
                Log.d("DB_ERROR",e.toString())
            }
        }

        fun passwordResetMail(mail:String, callback: (String) -> Unit)
        {
            auth = FirebaseAuth.getInstance()

            try {
                auth.sendPasswordResetEmail(mail).addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback("true")
                    } else {
                        if (it.exception is FirebaseAuthInvalidUserException) {
                            callback("exist")
                        } else {
                            callback("false")
                        }
                    }
                }.addOnFailureListener {
                    callback("false")
                }
            }catch(e:Exception)
            {
                Log.d("DB_ERROR",e.toString())
            }
        }
    }
}