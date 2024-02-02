package adapters

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class DatabaseAdapter {

    //Static methods and variables
    companion object{
        private lateinit var auth:FirebaseAuth
        lateinit var db:FirebaseDatabase

        fun returnUser():FirebaseUser?
        {
            auth = FirebaseAuth.getInstance()

            return auth.currentUser
        }

        fun verifyEmail(mail:String):Boolean{
            var isDone:Boolean = false

            auth = FirebaseAuth.getInstance()

            val user = auth.currentUser

            try {
                user?.sendEmailVerification()?.addOnCompleteListener {
                    isDone = it.isSuccessful
                }
            }catch (e:Exception)
            {
                Log.d("DB_ERROR",e.toString())
            }

            return isDone
        }

        fun signUpWithMail(mail:String, password:String, callback:(Boolean) -> Unit)
        {
            auth = FirebaseAuth.getInstance()

            try {
                auth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        callback(true)
                    }
                }.addOnFailureListener {
                    callback(false)
                    Log.d("DB_ERROR",it.toString())
                }
            }catch (e:Exception)
            {
                Log.d("DB_ERROR",e.toString())
            }
        }
    }
}