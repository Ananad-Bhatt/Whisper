package project.social.whisper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

import okhttp3.OkHttpClient
import project.social.whisper.databinding.ActivityChatGptBinding

class ChatGptActivity : AppCompatActivity() {

    private val client = OkHttpClient()

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

    private val generativeModel = GenerativeModel(
        // For text-only input, use the gemini-pro model
        modelName = "gemini-pro",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = BuildConfig.GEMINI
    )

    private fun sendData() {
        if(b.edtChatActMessage.text.toString().isNotEmpty()) {
            val msg = b.edtChatActMessage.text.toString()

            chats.add(msg)
            a.notifyDataSetChanged()

            lifecycleScope.launch {
                val response = generativeModel.generateContent(msg)
                chats.add(response.text.toString())
                a.notifyDataSetChanged()
            }

            b.edtChatActMessage.text.clear()
        }
    }
}