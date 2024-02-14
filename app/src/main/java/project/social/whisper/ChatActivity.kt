package project.social.whisper

import adapters.DatabaseAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import project.social.whisper.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var key:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = ActivityChatBinding.inflate(layoutInflater)
        setContentView(b.root)

        val userName = intent.getStringExtra("userName")
        val imgUrl = intent.getStringExtra("imgUrl")
        key = intent.getStringExtra("key").toString()

        b.tvChatActUserName.text = userName
        Glide.with(this).load(imgUrl).into(b.imgChatActUserImage)

        b.imgChatActSend.setOnClickListener {

            if(b.edtChatActMessage.text.toString().isNotEmpty())
            {
                val msg = b.edtChatActMessage.text.toString()

                val senderId = DatabaseAdapter.returnUser()?.uid
                val recId = key

                val senderRoom = senderId + recId
                val receiverRoom = recId + senderId

                DatabaseAdapter.chatTable.child(senderRoom).child()

            }

        }
    }
}