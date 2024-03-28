package adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import models.ChatModel
import project.social.whisper.ImageViewActivity
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
    private val SENDER_CONTACT_TYPE = 7
    private val RECEIVER_CONTACT_TYPE = 8
    private val SENDER_LOCATION_TYPE = 9
    private val RECEIVER_LOCATION_TYPE = 10

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
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
            SENDER_VIEW_IMAGE_TYPE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.sender_image_layout, parent, false)
                SenderImageChatHolder(view)
            }
            SENDER_CONTACT_TYPE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.sender_contact_layout, parent, false)
                SenderContactHolder(view)
            }
            RECEIVER_CONTACT_TYPE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.receiver_contact_layout, parent, false)
                ReceiverContactHolder(view)
            }
            SENDER_LOCATION_TYPE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.sender_layout, parent, false)
                SenderChatHolder(view)
            }
            RECEIVER_LOCATION_TYPE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.receiver_layout, parent, false)
                ReceiverChatHolder(view)
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

                if(m.MESSAGE?.contains("location:17861")!!)
                {
                    h.senderTime.text = f.format(d)

                    val c = m.MESSAGE?.split(",")!!

                    h.senderMessage.text = "Click Here to Open Location in Map"

                    h.senderMainView.setOnClickListener {
                        val strUri =
                            "http://maps.google.com/maps?q=loc:${c[1]},${c[2]} (User Location)"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(strUri))

                        intent.setClassName(
                            "com.google.android.apps.maps",
                            "com.google.android.maps.MapsActivity"
                        )

                        context.startActivity(intent)
                    }

                }
                else {
                    Log.d("IMG_ERROR", "WTH${m.MESSAGE}")
                    h.senderMessage.text = m.MESSAGE
                    h.senderTime.text = f.format(d)

                    h.senderMainView.setOnLongClickListener {
                        val p = PopupMenu(context, h.senderMainView)

                        p.inflate(R.menu.chat_context_menu)
                        p.show()

                        p.setOnMenuItemClickListener {

                            when(it.itemId)
                            {
                                R.id.copy_context_menu -> {
                                    val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("chat", m.MESSAGE)
                                    clipboardManager.setPrimaryClip(clip)
                                }

                                R.id.del_chat_context_menu -> {

                                    val sender = GlobalStaticAdapter.key+GlobalStaticAdapter.key2
                                    val receiver = GlobalStaticAdapter.key2+GlobalStaticAdapter.key

                                    DatabaseAdapter.chatTable.child(receiver)
                                        .addListenerForSingleValueEvent(object: ValueEventListener{
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if(snapshot.exists())
                                                {
                                                    for(s in snapshot.children)
                                                    {
                                                        val t = s.child("TIMESTAMP")
                                                            .getValue(Long::class.java)!!

                                                        Log.d("DEL_ERROR", m.TIMESTAMP.toString())
                                                        Log.d("DEL_ERROR", t.toString())
                                                        if(t == m.TIMESTAMP)
                                                        {
                                                            Log.d("DEL_ERROR", "Hello1 ${s.key!!}")
                                                            DatabaseAdapter.chatTable
                                                                .child(receiver)
                                                                .child(s.key!!)
                                                                .removeValue().addOnCompleteListener {
                                                                    Toast.makeText(context,
                                                                        "Chat deleted", Toast.LENGTH_LONG).show()
                                                                }
                                                            Log.d("DEL_ERROR", "Hello2")
                                                            chats.removeAt(h.adapterPosition)
                                                            notifyItemRemoved(h.adapterPosition)

                                                            return
                                                        }
                                                    }
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {

                                            }

                                        })

                                    DatabaseAdapter.chatTable.child(sender)
                                        .addListenerForSingleValueEvent(object: ValueEventListener{
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if(snapshot.exists())
                                                {
                                                    for(s in snapshot.children)
                                                    {
                                                        val t = s.child("TIMESTAMP")
                                                            .getValue(Long::class.java)!!

                                                        if(t == m.TIMESTAMP)
                                                        {
                                                            DatabaseAdapter.chatTable
                                                                .child(sender)
                                                                .child(s.key!!)
                                                                .removeValue()

                                                            return

//                                                            chats.removeAt(h.adapterPosition)
//                                                            notifyItemRemoved(h.adapterPosition)
                                                        }
                                                    }
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {

                                            }

                                        })
                                }

                                R.id.del_all_chat_context_menu -> {
                                    val receiver = GlobalStaticAdapter.key2+GlobalStaticAdapter.key

                                    //DatabaseAdapter.chatTable.child(sender).removeValue()
                                    DatabaseAdapter.chatTable.child(receiver).removeValue()

                                    notifyItemRangeChanged(0,0)
                                }
                            }

                            true
                        }

                        true
                    }

                }
            }

            ReceiverChatHolder::class.java -> {
                val h = holder as ReceiverChatHolder

                if(m.MESSAGE?.contains("location:17861")!!)
                {
                    h.receiverTime.text = f.format(d)

                    val c = m.MESSAGE?.split(",")!!

                    h.receiverMessage.text = "Click Here to Open Location in Map"

                    h.receiverMainView.setOnClickListener {
                        val strUri =
                            "http://maps.google.com/maps?q=loc:${c[1]},${c[2]} (User Location)"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(strUri))

                        intent.setClassName(
                            "com.google.android.apps.maps",
                            "com.google.android.maps.MapsActivity"
                        )

                        context.startActivity(intent)
                    }

                }else {
                    h.receiverMessage.text = m.MESSAGE
                    h.receiverTime.text = f.format(d)
                }
            }

            SenderImageChatHolder::class.java -> {
                Log.d("IMG_ERROR","ABCD${m.MESSAGE.toString()}")

                val h = holder as SenderImageChatHolder
                Glide.with(context).load(Uri.parse(m.MESSAGE)).into(h.senderImgMessage)
                h.senderImgTime.text = f.format(d)

                h.senderImgMessage.setOnClickListener {
                    val i = Intent(context, ImageViewActivity::class.java)
                    i.putExtra("img",m.MESSAGE)
                    context.startActivity(i)
                }
            }

            ReceiverImageChatHolder::class.java -> {
                Log.d("IMG_ERROR",m.MESSAGE.toString())
                Log.d("IMG_ERROR",Uri.parse(m.MESSAGE).toString())
                val h = holder as ReceiverImageChatHolder
                Glide.with(context).load(m.MESSAGE).into(h.receiverImgMessage)
                h.receiverImgTime.text = f.format(d)

                h.receiverImgMessage.setOnClickListener {
                    val i = Intent(context, ImageViewActivity::class.java)
                    i.putExtra("img",m.MESSAGE)
                    context.startActivity(i)
                }
            }

            SenderContactHolder::class.java -> {
                val h = holder as SenderContactHolder

                h.time.text = f.format(d)

                val c = m.MESSAGE?.split(",")!!

                h.contactName.text = c[1]

                h.number.text = "Click Here to copy number"

                h.copyNumber.setOnClickListener {

                    val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("contact number", c[2])
                    clipboardManager.setPrimaryClip(clip)

                    h.number.text = "Copied!"
                }
            }

            ReceiverContactHolder::class.java -> {
                val h = holder as ReceiverContactHolder

                h.time.text = f.format(d)

                val c = m.MESSAGE?.split(",")!!

                h.contactName.text = c[1]

                h.number.text = "Click Here to copy number"

                h.copyNumber.setOnClickListener {

                    val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("contact number", c[2])
                    clipboardManager.setPrimaryClip(clip)

                    h.number.text = "Copied!"
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        Log.d("IMG_ERROR","aa${chats[position].MESSAGE!!}")
        return if(chats[position].SENDER_KEY.equals(GlobalStaticAdapter.key)) {
            if(chats[position].MESSAGE!!.contains("project.social.whisper"))
                SENDER_VIEW_IMAGE_TYPE
            else if(chats[position].MESSAGE!!.contains("contact:184641"))
                SENDER_CONTACT_TYPE
            else if(chats[position].MESSAGE!!.contains("location:17861"))
                SENDER_LOCATION_TYPE
            else
                SENDER_VIEW_TYPE
        } else {
            if(chats[position].MESSAGE!!.contains("project.social.whisper"))
                RECEIVER_VIEW_IMAGE_TYPE
            else if(chats[position].MESSAGE!!.contains("contact:184641"))
                RECEIVER_CONTACT_TYPE
            else if(chats[position].MESSAGE!!.contains("location:17861"))
                RECEIVER_LOCATION_TYPE
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
        val senderMainView = itemView.findViewById<ConstraintLayout>(R.id.cl_sender_layout)!!
    }

    class SenderImageChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderImgMessage = itemView.findViewById<ImageView>(R.id.iv_sender_image_chat)!!
        val senderImgTime = itemView.findViewById<TextView>(R.id.tv_sender_image_chat_time)!!
        //val senderImgMainView = itemView.findViewById<RelativeLayout>(R.id.rl_sender_image_layout)!!
    }

    class ReceiverChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiverMessage = itemView.findViewById<TextView>(R.id.tv_receiver_chat)!!
        val receiverTime = itemView.findViewById<TextView>(R.id.tv_receiver_chat_time)!!
        val receiverMainView = itemView.findViewById<LinearLayout>(R.id.ll_receiver_chat)!!
    }

    class ReceiverImageChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiverImgMessage = itemView.findViewById<ImageView>(R.id.iv_receiver_image_chat)!!
        val receiverImgTime = itemView.findViewById<TextView>(R.id.tv_receiver_image_chat_time)!!
        //val receiverImgMainView = itemView.findViewById<RelativeLayout>(R.id.rl_receiver_image_layout)!!
    }

    class SenderContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName = itemView.findViewById<TextView>(R.id.tv_sender_contact_chat)!!
        val copyNumber = itemView.findViewById<LinearLayout>(R.id.ll_sender_contact)!!
        val number = itemView.findViewById<TextView>(R.id.tv_sender_contact_number_chat)!!
        val time = itemView.findViewById<TextView>(R.id.tv_sender_contact_time)!!
    }

    class ReceiverContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName = itemView.findViewById<TextView>(R.id.tv_receiver_contact_chat)!!
        val copyNumber = itemView.findViewById<LinearLayout>(R.id.ll_receiver_contact)!!
        val number = itemView.findViewById<TextView>(R.id.tv_receiver_contact_number_chat)!!
        val time = itemView.findViewById<TextView>(R.id.tv_receiver_contact_time)!!
    }
}