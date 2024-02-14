package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import models.ChatRecyclerModel
import project.social.whisper.R

class ChatRecyclerViewAdapter(private val usersList:ArrayList<ChatRecyclerModel>) :
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
        holder.img.setImageResource(usersList[position].img)
        holder.title.text = usersList[position].title
        holder.subTitle.text = usersList[position].subTitle
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.chat_frag_img)
        val title: TextView = itemView.findViewById(R.id.chat_frag_title)
        val subTitle: TextView = itemView.findViewById(R.id.chat_frag_sub_title)
    }
}


