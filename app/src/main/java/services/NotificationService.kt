package services

import adapters.DatabaseAdapter
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import project.social.whisper.BuildConfig


class NotificationService : FirebaseMessagingService() {

    companion object {
         fun generateToken() {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    DatabaseAdapter.token = task.result
                } else {

                }
            }
        }

        fun callApi(message: String)
        {
            try {
                val client = OkHttpClient()
                val body = JSONObject()
                val notification = JSONObject()
                notification.put("title", "title")
                notification.put("body", "message")
                body.put("notification", notification)
                body.put(
                    "to",
                    "eHvk6QxTRx6at1D5z7oEs2:APA91bHMpBovhhnKbe95GQc0PqS9ufABt6Hy6zylTwdj16OABlC3h5bg9wdEvvBii-KFd9UbYPPewiF1THwrUi4k8tu9b-tjBqez0jjJXh5Zp8MkqvHcrpFt_Xp4XynEJFCPoggZtrsz"
                )

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = body.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("https://fcm.googleapis.com/fcm/send")
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer ${BuildConfig.FCM_KEY}")
                    .addHeader("Content-Type", "application/json; UTF-8")
                    .build()
                Log.d("AAAAAA","A")
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.d("AAAAAA", "Failed to send notification: ${response.body?.string()}")
                    } else {
                        Log.d("AAAAAA", "Notification sent successfully")
                    }
                }
            }catch(e:Exception)
            {
                Log.d("AAAAAA",e.toString())
            }

        }

//        fun sendNotification(message: String) {
//            try{
//                val jsonObject = JSONObject()
//                Log.d("EHHEheHED","Started")
//                val notificationObj = JSONObject()
//                notificationObj.put("title", DatabaseAdapter.returnUser()?.email)
//                notificationObj.put("body", message)
//                Log.d("EHHEheHED","Started + $message")
//                val dataObj = JSONObject()
//                dataObj.put("userId", DatabaseAdapter.key)
//
//                jsonObject.put("notification", notificationObj)
//                jsonObject.put("data", dataObj)
//                jsonObject.put("to", "eHvk6QxTRx6at1D5z7oEs2:APA91bHMpBovhhnKbe95GQc0PqS9ufABt6Hy6zylTwdj16OABlC3h5bg9wdEvvBii-KFd9UbYPPewiF1THwrUi4k8tu9b-tjBqez0jjJXh5Zp8MkqvHcrpFt_Xp4XynEJFCPoggZtrsz")
//                Log.d("EHHEheHED","Calling")
//                callApi(jsonObject)
//            }catch(e:Exception)
//            {
//                Log.d("EHHEheHED","Exception + ${e.toString()}")
//            }
//        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

}