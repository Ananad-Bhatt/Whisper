package services

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
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
                    GlobalStaticAdapter.fcmToken = task.result

                    DatabaseAdapter.userDetailsTable
                        .child(GlobalStaticAdapter.uid)
                        .child(GlobalStaticAdapter.key)
                        .child("FCM_TOKEN")
                        .setValue(task.result)

                }
            }
        }

        fun sendNotification(message: String, fcmToken: String, userName: String)
        {
            try {
                if(fcmToken != "") {
                    val client = OkHttpClient()
                    val body = JSONObject()
                    val notification = JSONObject()
                    notification.put("title", userName)
                    notification.put("body", message)
                    body.put("notification", notification)
                    body.put(
                        "to",
                        fcmToken
                    )

                    val mediaType = "application/json; charset=utf-8".toMediaType()
                    val requestBody = body.toString().toRequestBody(mediaType)

                    val request = Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .post(requestBody)
                        .addHeader("Authorization", "Bearer ${BuildConfig.FCM_KEY}")
                        .addHeader("Content-Type", "application/json; UTF-8")
                        .build()
                    Log.d("AAAAAA", "A")
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            Log.d(
                                "AAAAAA",
                                "Failed to send notification: ${response.body?.string()}"
                            )
                        } else {
                            Log.d("AAAAAA", "Notification sent successfully")
                        }
                    }
                }
            }catch(e:Exception)
            {
                Log.d("AAAAAA",e.toString())
            }

        }
    }

}