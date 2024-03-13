package services

import adapters.DatabaseAdapter
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class NotificationService : FirebaseMessagingService() {

    companion object {
        suspend fun generateToken() :String = suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(task.result)
                } else {
                    continuation.resumeWithException(task.exception ?: RuntimeException("Unknown error"))
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

}