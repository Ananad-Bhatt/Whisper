package adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import models.ChatAIModel
import models.ChatModel
import project.social.whisper.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AIAdapter (private val context: Context, private val chats:ArrayList<ChatAIModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SENDER_VIEW_TYPE = 1
    private val RECEIVER_VIEW_TYPE = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType) {
            SENDER_VIEW_TYPE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.sender_layout, parent, false)
                SenderChatHolder(view)
            }

            RECEIVER_VIEW_TYPE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.receiver_layout, parent, false)
                ReceiverChatHolder(view)
            }

            else -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.sender_layout, parent, false)
                SenderChatHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val m = chats[position]
        val d = Date(m.TIMESTAMP!!)

        val f = SimpleDateFormat("hh:mm a", Locale.getDefault())

        when (holder.javaClass) {
            SenderChatHolder::class.java -> {
                val h = holder as SenderChatHolder

                h.senderMessage.text = m.MESSAGE
                Log.d("CHAT_AI", h.senderMessage.text.toString())

                h.senderTime.text = f.format(d)

                h.senderMainView.setOnLongClickListener {
                    val p = PopupMenu(context, h.senderMainView)

                    p.inflate(R.menu.chat_menu_ai)
                    p.show()

                    p.setOnMenuItemClickListener {

                        when (it.itemId) {
                            R.id.copy_context_menu -> {
                                val clipboardManager =
                                    context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("chat", m.MESSAGE)
                                clipboardManager.setPrimaryClip(clip)
                            }

                            R.id.del_chat_context_menu -> {
                                delFromMe(m)
                                chats.removeAt(h.adapterPosition)
                                notifyItemRemoved(h.adapterPosition)
                            }

                            R.id.del_all_chat_context_menu -> {
                                chats.removeAll(chats.toSet())
                                notifyItemRangeChanged(0, 0)
                            }
                        }
                        true
                    }

                    true
                }
            }

            ReceiverChatHolder::class.java -> {
                val h = holder as ReceiverChatHolder

                h.receiverMainView.setOnLongClickListener {
                    val p = PopupMenu(context, h.receiverMainView)

                    p.inflate(R.menu.chat_menu_ai)
                    p.show()

                    p.setOnMenuItemClickListener {

                        when (it.itemId) {
                            R.id.copy_context_menu -> {
                                val clipboardManager =
                                    context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("chat", m.MESSAGE)
                                clipboardManager.setPrimaryClip(clip)
                            }

                            R.id.del_chat_context_menu -> {
                                delFromMe(m)
                                chats.removeAt(h.adapterPosition)
                                notifyItemRemoved(h.adapterPosition)
                            }

                            R.id.del_all_chat_context_menu -> {
                                chats.removeAll(chats.toSet())
                                notifyItemRangeChanged(0, 0)
                            }
                        }
                        true
                    }

                    true
                }

                h.receiverMessage.text = m.MESSAGE
                h.receiverTime.text = f.format(d)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(!chats[position].isAI!!) {
                SENDER_VIEW_TYPE
        } else {
                RECEIVER_VIEW_TYPE
        }
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

    private fun delFromMe(m: ChatAIModel)
    {

    }
}