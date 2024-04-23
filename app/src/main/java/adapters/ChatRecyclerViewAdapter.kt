package adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import models.ChatRecyclerModel
import project.social.whisper.ChatActivity
import project.social.whisper.R

class ChatRecyclerViewAdapter(private val context: Context, private val usersList:ArrayList<ChatRecyclerModel>) :
    RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_recycler_view, parent, false)

        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        Glide.with(context).load(usersList[position].img).into(holder.img)
        holder.title.text = usersList[position].title

        if(!usersList[position].subTitle.contains("https://firebasestorage.googleapis.com"))
            holder.subTitle.text = "Text Message"
        else
            holder.subTitle.text = "Image"

        holder.ll.setOnClickListener {
            val i = Intent(context, ChatActivity::class.java)
            GlobalStaticAdapter.userName2 = usersList[position].title
            GlobalStaticAdapter.imageUrl2 = usersList[position].img
            GlobalStaticAdapter.key2 = usersList[position].key
            GlobalStaticAdapter.uid2 = usersList[position].uid
            GlobalStaticAdapter.fcmToken2 = usersList[position].fcm
            GlobalStaticAdapter.accountType2 = usersList[position].type
            GlobalStaticAdapter.about2 = usersList[position].about
            context.startActivity(i)
        }
    }
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.chat_frag_img)
        val title: TextView = itemView.findViewById(R.id.chat_frag_title)
        val subTitle: TextView = itemView.findViewById(R.id.chat_frag_sub_title)
        val ll: LinearLayout = itemView.findViewById(R.id.ll_chat_frag)
    }
}


