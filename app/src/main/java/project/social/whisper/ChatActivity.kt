package project.social.whisper

import adapters.ChatAdapter
import adapters.DatabaseAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import models.ChatModel
import project.social.whisper.databinding.ActivityChatBinding
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private lateinit var b:ActivityChatBinding

    private lateinit var key:String

    private lateinit var senderId:String
    private lateinit var recId:String

    private lateinit var senderRoom:String
    private lateinit var receiverRoom:String

    private var chats:ArrayList<ChatModel> = ArrayList()

    private var chatAdapter:ChatAdapter = ChatAdapter(this, chats)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityChatBinding.inflate(layoutInflater)
        setContentView(b.root)

        key = intent.getStringExtra("key").toString()

        senderId = DatabaseAdapter.returnUser()?.uid!!
        recId = key

        senderRoom = senderId + recId
        receiverRoom = recId + senderId


        val lManager = LinearLayoutManager(this)
        lManager.stackFromEnd = true
        b.rvChatAct.layoutManager = lManager
        b.rvChatAct.adapter = chatAdapter

        b.rvChatAct.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                b.rvChatAct.scrollBy(0, oldBottom - bottom);
            }
        }

        receiveData()
        sendData()

        val userName = intent.getStringExtra("userName")
        val imgUrl = intent.getStringExtra("imgUrl")

        b.tvChatActUserName.text = userName
        Glide.with(this).load(imgUrl).into(b.imgChatActUserImage)
    }

    private fun receiveData() {
        try {
            DatabaseAdapter.chatTable.child(senderRoom).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chats.clear()

                    if(snapshot.exists()) {
                        for (s in snapshot.children) {
                            val data: ChatModel = s.getValue(ChatModel::class.java)!!
                            chats.add(data)
                        }
                    }
                    chatAdapter.notifyItemInserted(chats.size)
                    b.rvChatAct.scrollToPosition(chatAdapter.itemCount-1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_LONG).show()
                }
            })
        }catch(e:Exception)
        {
            Log.d("DB_ERROR",e.toString())
        }
    }

    private fun sendData()
    {
        b.imgChatActSend.setOnClickListener {

            if(b.edtChatActMessage.text.toString().isNotEmpty())
            {
                val msg = b.edtChatActMessage.text.toString()

                val chatMap = HashMap<String, Any>()
                chatMap["USER_KEY"] = senderId
                chatMap["MESSAGE"] = msg
                chatMap["TIMESTAMP"] = Date().time


                DatabaseAdapter.chatTable.child(senderRoom).push().setValue(chatMap)
                DatabaseAdapter.chatTable.child(receiverRoom).push().setValue(chatMap)

                b.edtChatActMessage.text.clear()
            }
        }
    }
}