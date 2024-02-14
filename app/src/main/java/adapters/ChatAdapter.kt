package adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import models.ChatModel

class ChatAdapter(private val context: Context, private val chats:ArrayList<ChatModel>) :
    RecyclerView.Adapter<ChatAdapter.ChatHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ChatHolder {
        TODO()
    }

    override fun onBindViewHolder(holder: ChatAdapter.ChatHolder, position: Int) {
        TODO()
    }

    override fun getItemViewType(position: Int): Int {
        TODO()
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    class ChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


}