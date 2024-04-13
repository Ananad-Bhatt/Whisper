package project.social.whisper

import adapters.AIAdapter
import adapters.ChatAdapter
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch
import models.ChatAIModel
import models.ChatModel

import okhttp3.OkHttpClient
import project.social.whisper.databinding.ActivityChatGptBinding
import java.util.Date

class ChatGptActivity : BaseActivity() {

    private lateinit var b: ActivityChatGptBinding

    private var chats:ArrayList<ChatAIModel> = ArrayList()
    private val a = AIAdapter(this, chats)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityChatGptBinding.inflate(layoutInflater)
        setContentView(b.root)

        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true

        b.rvChatAct.layoutManager =manager

        b.rvChatAct.adapter = a


        b.imgChatActSend.setOnClickListener {
            sendData()
        }
    }

    private val generativeModel = GenerativeModel(
        // For text-only input, use the gemini-pro model
        modelName = "gemini-pro",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = BuildConfig.GEMINI
    )

    private fun sendData() {
        if(b.edtChatActMessage.text.toString().isNotEmpty()) {
            val msg = b.edtChatActMessage.text.toString()

            chats.add(ChatAIModel(msg, Date().time, false))
            a.notifyItemInserted(chats.size)
            b.rvChatAct.scrollToPosition(a.itemCount - 1)

            lifecycleScope.launch {
                val response = generativeModel.generateContent(msg)
                chats.add(ChatAIModel(response.text.toString(), Date().time, true))
                a.notifyItemInserted(chats.size)
                b.rvChatAct.scrollToPosition(a.itemCount - 1)
            }

            b.edtChatActMessage.text.clear()
        }
    }

    override fun getSelectedTheme(): String {
        val sharedPreferences = getSharedPreferences("app_theme", MODE_PRIVATE)
        return sharedPreferences.getString("theme", "primary1")?: "primary1"
    }

    override fun getWhiteOrBlackTheme(): String {
        val sharedPreferences = getSharedPreferences("app_theme_wb", MODE_PRIVATE)
        return sharedPreferences.getString("theme_wb", "system")?: "system"
    }
}