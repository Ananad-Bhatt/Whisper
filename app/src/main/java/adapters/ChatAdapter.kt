package adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import models.ChatModel
import project.social.whisper.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(private val context: Context, private val chats:ArrayList<ChatModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SENDER_VIEW_TYPE = 1
    private val RECEIVER_VIEW_TYPE = 2
    private val SENDER_VIEW_IMAGE_TYPE = 3
    private val SENDER_VIEW_VIDEO_TYPE = 4
    private val RECEIVER_VIEW_IMAGE_TYPE = 5
    private val RECEIVER_VIEW_VIDEO_TYPE = 6

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SENDER_VIEW_TYPE -> {
                Log.d("HIIII","a")
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.sender_layout, parent, false)

                Log.d("HIIII","ab")

                SenderChatHolder(view)
            }
            RECEIVER_VIEW_TYPE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.receiver_layout, parent, false)
                ReceiverChatHolder(view)
            }
            SENDER_VIEW_IMAGE_TYPE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.sender_image_layout, parent, false)
                SenderImageChatHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.receiver_image_layout, parent, false)
                ReceiverImageChatHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val m = chats[position]
        val d = Date(m.TIMESTAMP!!)

        val f = SimpleDateFormat("hh:mm a", Locale.getDefault())

        when (holder.javaClass) {
            SenderChatHolder::class.java -> {
                val h = holder as SenderChatHolder
                h.senderMessage.text = m.MESSAGE
                h.senderTime.text = f.format(d)
            }
            ReceiverChatHolder::class.java -> {
                val h = holder as ReceiverChatHolder
                h.receiverMessage.text = m.MESSAGE
                h.receiverTime.text = f.format(d)
            }
            SenderImageChatHolder::class.java -> {
                val h = holder as SenderImageChatHolder
                Glide.with(context).load(m.MESSAGE).into(h.senderImgMessage)
                h.senderImgTime.text = f.format(d)
            }
            ReceiverImageChatHolder::class.java -> {
                val h = holder as ReceiverImageChatHolder
                Glide.with(context).load(m.MESSAGE).into(h.receiverImgMessage)
                h.receiverImgTime.text = f.format(d)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(chats[position].SENDER_KEY.equals(DatabaseAdapter.key)) {
            if(chats[position].MESSAGE!!.contains("https://firebasestorage.googleapis.com"))
                SENDER_VIEW_IMAGE_TYPE
            else
                SENDER_VIEW_TYPE
        } else {
            if(chats[position].MESSAGE!!.contains("https://firebasestorage.googleapis.com"))
                RECEIVER_VIEW_IMAGE_TYPE
            else
                RECEIVER_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    class SenderChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderMessage = itemView.findViewById<TextView>(R.id.tv_sender_chat)!!
        val senderTime = itemView.findViewById<TextView>(R.id.tv_sender_chat_time)!!
        //val senderMainView = itemView.findViewById<RelativeLayout>(R.id.rl_sender_layout)!!
    }

    class SenderImageChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderImgMessage = itemView.findViewById<ImageView>(R.id.iv_sender_image_chat)!!
        val senderImgTime = itemView.findViewById<TextView>(R.id.tv_sender_image_chat_time)!!
        //val senderImgMainView = itemView.findViewById<RelativeLayout>(R.id.rl_sender_image_layout)!!
    }

    class ReceiverChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiverMessage = itemView.findViewById<TextView>(R.id.tv_receiver_chat)!!
        val receiverTime = itemView.findViewById<TextView>(R.id.tv_receiver_chat_time)!!
        //val receiverMainView = itemView.findViewById<RelativeLayout>(R.id.rl_receiver_layout)!!
    }

    class ReceiverImageChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiverImgMessage = itemView.findViewById<ImageView>(R.id.iv_receiver_image_chat)!!
        val receiverImgTime = itemView.findViewById<TextView>(R.id.tv_receiver_image_chat_time)!!
        //val receiverImgMainView = itemView.findViewById<RelativeLayout>(R.id.rl_receiver_image_layout)!!
    }
}