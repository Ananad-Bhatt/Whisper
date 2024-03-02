package adapters

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class DatabaseAdapter {

    //Static methods and variables
    companion object{
        private lateinit var auth:FirebaseAuth

        //DB tables
        var usersTable = Firebase.database.getReference("USERS")
        var userDetailsTable = Firebase.database.getReference("USER_DETAILS")
        var chatTable = Firebase.database.getReference("CHATS")
        var chatRooms = Firebase.database.getReference("CHAT_ROOMS")
        var keysTable = Firebase.database.getReference("KEYS")

        //Storage
        var userImage = FirebaseStorage.getInstance().getReference("USER_IMAGES")
        var chatImage = FirebaseStorage.getInstance().getReference("CHAT_IMAGES")

        //Current user key
        var key = ""

        //Global variables
        var contactName = ""
        var contactNumber = ""

        //Encryption Key
        private lateinit var encryptionKey:ByteArray

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


        //Generate Encryption Key
         fun generateEncryptionKey(email: String, privateKey: String, chatRoom: String) {
            val staticSalt = "wqughv fed7^&@!(*vhjQW1254537/AFDMNQgewf;wf;g u gyGFYEGIDBSIAFWAG JIQW87R2378RGBF7jbf sd/54f7da7wa bjfgw iqyfgwdyhaf0912834=576 baHFGBHG%^%^q#&GGFGGUw $chatRoom".toByteArray() // Choose a static salt
            val iterations = 10000 // Number of iterations for key stretching
            val keyLength = 256 // Length of the derived key in bits

            // Use email address as input to the KDF
            val input = email.toCharArray()

            try {
                val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                val spec = PBEKeySpec(input, staticSalt, iterations, keyLength)
                val secretKey = skf.generateSecret(spec)
                encryptionKey = SecretKeySpec(secretKey.encoded, "AES").encoded

                encryptPrivateKeyAndUpload(privateKey, chatRoom)

            } catch (e: Exception) {
                throw RuntimeException("Error deriving encryption key", e)
            }
        }

        private fun encryptPrivateKeyAndUpload(privateKey:String, chatRoom:String)
        {
            val encryptedPrivateKey = encryptPrivateKey(privateKey)

            uploadKeyToDB(encryptedPrivateKey, chatRoom)
        }

        private fun uploadKeyToDB(encryptedPrivateKey: String, chatRoom:String) {
            try {
                keysTable.child(chatRoom).child("KEY").setValue(encryptedPrivateKey)
            }catch(e:Exception)
            {
                Log.d("DB_ERROR","ERROR STORING KEY")
            }
        }

        private fun encryptPrivateKey(privateKey:String) : String
        {
            val encode:Cipher

            val skp = SecretKeySpec(encryptionKey, "AES")

            val messageByte = privateKey.toByteArray()

            val encodedByte: ByteArray

            try {
                encode = Cipher.getInstance("AES")

                encodedByte = try {
                    encode.init(Cipher.ENCRYPT_MODE, skp)
                    encode.doFinal(messageByte)
                } catch (e: Exception) {
                    Log.d("Exception", e.toString())
                    throw RuntimeException(e)
                }

                return String(encodedByte, StandardCharsets.ISO_8859_1)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        private fun decryptPrivateKey(privateKey:String) : String
        {
            val decode:Cipher

            val skp = SecretKeySpec(encryptionKey, "AES")

            val encodedByte: ByteArray = privateKey.toByteArray(StandardCharsets.ISO_8859_1)

            val decodedString: String

            val decoding: ByteArray

            try{
                decode = Cipher.getInstance("AES")

                try {
                    decode.init(Cipher.DECRYPT_MODE, skp)
                    decoding = decode.doFinal(encodedByte)
                    decodedString = String(decoding)

                    return decodedString
                } catch (e: Exception) {
                    Log.d("KEY_ERROR","Unable to decode")
                }
            }catch(e:Exception){
                Log.d("KEY_ERROR","Something went wrong")
            }
            return ""
        }
    }
}