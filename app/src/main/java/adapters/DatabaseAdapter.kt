package adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigInteger
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64
import java.util.concurrent.Executors
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


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

        //Global variables
        var contactName = ""
        var contactNumber = ""

        //Encryption Key
        private lateinit var encryptionKey:ByteArray

        //p and g in Diffie-Hellman
        val p:BigInteger = BigInteger("5147")
        private val g = BigInteger("3")

        private val staticIv = "0123456789abcdef".toByteArray()

        var i = 0

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
             try {
                 val secretKey = generateSecretKeyFromEmail(email, chatRoom)
                 encryptionKey = SecretKeySpec(secretKey.encoded, "AES").encoded

                 encryptPrivateKeyAndUpload(privateKey, chatRoom, email)
             }catch(e: Exception)
             {
                 e.printStackTrace()
             }
        }

        private fun generateSecretKeyFromEmail(email: String, chatRoom: String) : SecretKey
        {
            val staticSalt = "wqughv fed7^&@!(*vhjQW1254537/AFDMNQgewf;wf;g u gyGFYEGIDBSIAFWAG JIQW87R2378RGBF7jbf sd/54f7da7wa bjfgw iqyfgwdyhaf0912834=576 baHFGBHG%^%^q#&GGFGGUw $chatRoom".toByteArray() // Choose a static salt
            val iterations = 10000 // Number of iterations for key stretching
            val keyLength = 256 // Length of the derived key in bits

            // Use email address as input to the KDF
            val input = email.toCharArray()

            try {
                val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                val spec = PBEKeySpec(input, staticSalt, iterations, keyLength)
                return skf.generateSecret(spec)
            }catch(e:Exception) {
                e.printStackTrace()
            }
            return "" as SecretKey
        }

        private fun encryptPrivateKeyAndUpload(privateKey:String, chatRoom:String, email: String)
        {
            Log.d("HASD",privateKey)
            val encryptedPrivateKey = encryptPrivateKey(privateKey)

            uploadKeyToDB(encryptedPrivateKey, chatRoom, email)
        }

        private fun uploadKeyToDB(encryptedPrivateKey: String, chatRoom:String, email: String) {
            try {
                keysTable.child(chatRoom).child("KEY").setValue(encryptedPrivateKey)
                    .addOnSuccessListener {
                        keysTable.child(chatRoom).child("KEY").addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val k = snapshot.getValue(String::class.java)!!
                                Log.d("QWEASDZXC","p:$encryptedPrivateKey")
                                Log.d("QWEASDZXC","q:$k")
                                Log.d("QWEASDZXC","r:${decryptPrivateKey(encryptedPrivateKey, chatRoom, email)}")
                                Log.d("QWEASDZXC","s:${decryptPrivateKey(k,chatRoom,email)}")
                                generatePublicKey(encryptedPrivateKey, chatRoom, email)
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })
                    }
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

        fun decryptPrivateKey(privateKey:String, chatRoom: String, email: String) : String
        {
            val decode:Cipher

            Log.d("QWEASDZXC","fetch3:$privateKey")

            val secretKey = generateSecretKeyFromEmail(email, chatRoom)
            val eKey = SecretKeySpec(secretKey.encoded, "AES").encoded

            val skp = SecretKeySpec(eKey, "AES")

            val encodedByte: ByteArray = privateKey.toByteArray(StandardCharsets.ISO_8859_1)

            val decodedString: String

            val decoding: ByteArray

            Log.d("QWEASDZXC","fetch4:$encodedByte")

            try{
                decode = Cipher.getInstance("AES")

                try {
                    decode.init(Cipher.DECRYPT_MODE, skp)
                    decoding = decode.doFinal(encodedByte)
                    decodedString = String(decoding)

                    return decodedString
                } catch (e: Exception) {
                    Log.d("KEY_ERROR",e.toString())
                }
            }catch(e:Exception){
                Log.d("KEY_ERROR","Something went wrong")
            }
            return "qwe"
        }

        //Generating Public key
        private fun generatePublicKey(privateKey: String, chatRoom: String, email: String)
        {
            val a = BigInteger(decryptPrivateKey(privateKey, chatRoom, email))
            val publicKeyA = g.pow(a.toInt()) % p

            uploadPublicKeyToDB(publicKeyA.toString(), chatRoom)
        }

        private fun uploadPublicKeyToDB(publicKeyA: String, chatRoom: String) {
            try {
                keysTable.child(chatRoom).child("PUBLIC_KEY").setValue(publicKeyA)
            }catch(e:Exception)
            {
                e.printStackTrace()
            }
        }

        fun generateRandomKey(): String{
            return BigInteger(12, SecureRandom()).toString()
        }

        fun encryptMessage(message:String, sharedEncryptionKey:ByteArray):String
        {
            val newKey = makeSureKeySize(sharedEncryptionKey)

            val encode:Cipher

            val skp = SecretKeySpec(newKey, "AES")

            val messageByte = message.toByteArray()

            val encodedByte: ByteArray

            try {
                encode = Cipher.getInstance("AES")

                encodedByte = try {
                    encode.init(Cipher.ENCRYPT_MODE, skp)
                    encode.doFinal(messageByte)
                } catch (e: Exception) {
                    Log.d("SPEXC",e.toString())
                    throw RuntimeException(e)
                }

                return Base64.getEncoder().encodeToString(encodedByte)

            } catch (e: Exception) {
                Log.d("SPEXC",e.toString())
            }
            return ""
        }

        fun decryptMessage(message:String, sharedEncryptionKey:ByteArray):String
        {
            val newKey = makeSureKeySize(sharedEncryptionKey)

            val decode:Cipher

            val skp = SecretKeySpec(newKey, "AES")

            val messageByte = Base64.getDecoder().decode(message)

            val decodedString:String

            val decodedByte:ByteArray

            try {
                decode = Cipher.getInstance("AES")

                decodedByte = try {
                    decode.init(Cipher.DECRYPT_MODE, skp)
                    decode.doFinal(messageByte)
                } catch (e: Exception) {
                    Log.d("SPEXC",e.toString())
                    throw RuntimeException(e)
                }
                decodedString = String(decodedByte)
                return decodedString

            } catch (e: Exception) {
                Log.d("SPEXC",e.toString())
            }
            return ""
        }

        fun encryptImage(imgUri: Uri, sharedEncryptionKey: ByteArray, context: Context): Uri? {
            val newKey = makeSureKeySize(sharedEncryptionKey)
            val inputImage = convertUriToByte(imgUri, context)
            Log.d("IMG_ERROR", "Input Image Size: ${inputImage.size}")
            try {
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                val secretKey = SecretKeySpec(newKey, "AES")
                val ivSpec = IvParameterSpec(staticIv)
                Log.d("IMG_ERROR", "Secret Key: $secretKey")
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
                val encryptedData = cipher.doFinal(inputImage)
                Log.d("IMG_ERROR", "Encrypted Data Size: ${encryptedData.size}")
                val base64EncryptedData = Base64.getEncoder().encodeToString(encryptedData)
                return convertByteToUri(context, base64EncryptedData.toByteArray())
            } catch (e: Exception) {
                Log.e("KEY_ERROR", "Encryption Error: ${e.message}")
            }
            return null
        }

        fun decryptImage(imgUri: Uri, sharedEncryptionKey: ByteArray, context: Context): Uri? {
            val newKey = makeSureKeySize(sharedEncryptionKey)
            val inputImage = convertUriToByte(imgUri, context)
            Log.d("IMG_ERROR", "Input Encrypted Image Size: ${inputImage.size}")
            try {
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                val secretKey = SecretKeySpec(newKey, "AES")
                val ivSpec = IvParameterSpec(staticIv)
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
                val decryptedData = cipher.doFinal(Base64.getDecoder().decode(inputImage))
                Log.d("IMG_ERROR", "Decrypted Data Size: ${decryptedData.size}")
                return convertByteToUri(context, decryptedData)
            } catch (e: Exception) {
                Log.e("KEY_ERROR", "Decryption Error: ${e.message}")
            }
            return null
        }

        private fun convertUriToByte(uri:Uri, context:Context):ByteArray
        {
            val inputStream = context.contentResolver.openInputStream(uri)
            val byteBuffer = ByteArrayOutputStream()
            inputStream?.use { input ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    byteBuffer.write(buffer, 0, bytesRead)
                }
            }
            return byteBuffer.toByteArray()
        }

        private fun convertByteToUri(context: Context, byteArray: ByteArray): Uri? {
            // Create a temporary file to save the byte array data
            val tempFile = File(context.cacheDir, "temp$i.jpg")
            i++
            try {
                FileOutputStream(tempFile).use { outputStream ->
                    outputStream.write(byteArray)
                }
                // Return the URI for the temporary file
                return FileProvider.getUriForFile(context, context.packageName + ".provider", tempFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        suspend fun downloadImageAndConvertToUri(context: Context, imageUrl: String, fileName:String): Uri = suspendCancellableCoroutine { continuation ->
            val executor = Executors.newSingleThreadExecutor()

            executor.execute {
                try {
                    val tempFile = File.createTempFile(fileName, null, context.cacheDir)
                    val url = URL(imageUrl)

                    url.openStream().use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }

                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        tempFile
                    )
                    continuation.resume(uri)
                } catch (e: IOException) {
                    continuation.resumeWithException(e)
                }
            }

            executor.shutdown()
        }

        private fun makeSureKeySize(sharedEncryptionKey: ByteArray): ByteArray {

            Log.d("SPEXC",sharedEncryptionKey.toHashSet().toString())

            // Create a new byte array of the desired length (16 bytes for 128 bits)
            val paddedBytes = ByteArray(32)
            // Copy the bytes of the original private key into the padded array
            System.arraycopy(sharedEncryptionKey, 0, paddedBytes, 0, sharedEncryptionKey.size)
            // Optionally, fill the remaining bytes with a specific value (e.g., 0, 1, or 5)
            val fillValue = 99.toByte() // Change this value as needed
            for (i in sharedEncryptionKey.size until paddedBytes.size) {
                paddedBytes[i] = fillValue
            }
            Log.d("SPEXC",paddedBytes.size.toString())
            return paddedBytes
        }

        fun deleteTempFiles(file: File): Boolean {
            if (file.isDirectory) {
                val files = file.listFiles()
                if (files != null) {
                    for (f in files) {
                        if (f.isDirectory) {
                            deleteTempFiles(f)
                        } else {
                            f.delete()
                        }
                    }
                }
            }
            return file.delete()
        }
    }
}