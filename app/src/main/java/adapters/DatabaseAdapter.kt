package adapters

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.nio.charset.StandardCharsets
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
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

        //Storing Key
        private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        //Current user key
        var key = ""

        //Global variables
        var contactName = ""
        var contactNumber = ""

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

        fun generateAndStorePrivateKey(keyAlias:String) {
            if(!isKeyAliasExists(keyAlias)) {
                val keyPairGenerator =
                    KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
                val builder = KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
                )
                    .setKeySize(2048)
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setAlgorithmParameterSpec(null)

                keyPairGenerator.initialize(builder.build())
                keyPairGenerator.generateKeyPair()
            }
        }

        fun retrievePrivateKey(keyAlias:String): ByteArray? {
            val privateKeyEntry = keyStore.getEntry(keyAlias, null) as? KeyStore.PrivateKeyEntry
            return privateKeyEntry?.privateKey?.encoded
        }

        private fun isKeyAliasExists(keyAlias:String): Boolean {
            return try {
                keyStore.containsAlias(keyAlias)
            } catch (e: Exception) {
                false
            }
        }

        //Generate Encryption Key
        private fun generateSecureRandomBytes(): ByteArray {
            val secureRandom = SecureRandom()
            val randomBytes = ByteArray(32)
            secureRandom.nextBytes(randomBytes)
            return randomBytes
        }

        //Generating Byte Array
        fun generateAndEncryptByteArray()
        {
            val encryptionKey = generateSecureRandomBytes()
            val encryptedEncryptionKey = encryptPrivateKey(encryptionKey)

        }

        private fun encryptPrivateKey(encryptionKey:ByteArray, privateKey:String) : String
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
        }
    }
}