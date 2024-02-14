package project.social.whisper

import adapters.DatabaseAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import models.ChatModel
import project.social.whisper.databinding.ActivityChatBinding
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private lateinit var b:ActivityChatBinding

    private lateinit var key:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityChatBinding.inflate(layoutInflater)
        setContentView(b.root)

        receiveData()
        sendData()

        val userName = intent.getStringExtra("userName")
        val imgUrl = intent.getStringExtra("imgUrl")
        key = intent.getStringExtra("key").toString()

        b.tvChatActUserName.text = userName
        Glide.with(this).load(imgUrl).into(b.imgChatActUserImage)
    }

    private fun receiveData() {

    }

    private fun sendData()
    {
        b.imgChatActSend.setOnClickListener {

            if(b.edtChatActMessage.text.toString().isNotEmpty())
            {
                val msg = b.edtChatActMessage.text.toString()

                val senderId = DatabaseAdapter.returnUser()?.uid
                val recId = key

                val senderRoom = senderId + recId
                val receiverRoom = recId + senderId

                val model = ChatModel(senderId,msg, Date().time)

                DatabaseAdapter.chatTable.child(senderRoom).push().setValue(model)
                DatabaseAdapter.chatTable.child(receiverRoom).push().setValue(model)

                b.edtChatActMessage.text.clear()
            }
        }
    }
}