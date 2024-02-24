package adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.github.hyuwah.draggableviewlib.DraggableView.*
import models.ChatModel
import project.social.whisper.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(private val context: Context, private val chats:ArrayList<ChatModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SENDER_VIEW_TYPE = 1
    private val RECEIVER_VIEW_TYPE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == SENDER_VIEW_TYPE) {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.sender_layout, parent, false)
            SenderChatHolder(view)
        } else {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.receiver_layout, parent, false)
            ReceiverChatHolder(view)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val m = chats[position]
        val d = Date(m.TIMESTAMP!!)

        val f = SimpleDateFormat("hh:mm a", Locale.getDefault())

        if(holder.javaClass == SenderChatHolder::class.java)
        {
            val h = holder as SenderChatHolder
            h.senderMessage.text = m.MESSAGE
            h.senderTime.text = f.format(d)
        }

        else
        {
            val h = holder as ReceiverChatHolder
            h.receiverMessage.text = m.MESSAGE
            h.receiverTime.text = f.format(d)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(chats[position].SENDER_KEY.equals(DatabaseAdapter.key)) {
            SENDER_VIEW_TYPE
        } else {
            RECEIVER_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    class SenderChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderMessage = itemView.findViewById<TextView>(R.id.tv_sender_chat)!!
        val senderTime = itemView.findViewById<TextView>(R.id.tv_sender_chat_time)!!
        val senderMainView = itemView.findViewById<ConstraintLayout>(R.id.cl_sender_layout)!!
    }

    class ReceiverChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiverMessage = itemView.findViewById<TextView>(R.id.tv_receiver_chat)!!
        val receiverTime = itemView.findViewById<TextView>(R.id.tv_receiver_chat_time)!!
        val receiverMainView = itemView.findViewById<LinearLayout>(R.id.ll_receiver_chat)!!
    }
}