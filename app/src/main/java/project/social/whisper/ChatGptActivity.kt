package project.social.whisper

import adapters.DatabaseAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.ChatModel
import project.social.whisper.databinding.ActivityChatBinding
import project.social.whisper.databinding.ActivityChatGptBinding
import services.NotificationService
import java.util.Date

class ChatGptActivity : AppCompatActivity() {

    private lateinit var b: ActivityChatGptBinding

    private var chats:ArrayList<String> = ArrayList()

    private lateinit var a:ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityChatGptBinding.inflate(layoutInflater)
        setContentView(b.root)

        a = ArrayAdapter(this, android.R.layout.simple_list_item_1, chats)

        b.rvChatAct.adapter = a

        b.imgChatActSend.setOnClickListener {
            sendData()
        }
    }

    private fun sendData() {
        if(b.edtChatActMessage.text.toString().isNotEmpty()) {
            val msg = b.edtChatActMessage.text.toString()

            chats.add(msg)
            a.notifyDataSetChanged()

            b.edtChatActMessage.text.clear()
        }
    }
}