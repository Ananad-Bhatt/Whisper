package adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import models.SearchModel
import project.social.whisper.R
import project.social.whisper.UserProfileActivity

class SearchRecyclerViewAdapter(private val context:Context,private val searchResults:ArrayList<SearchModel>) :
    RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_recycler_view, parent, false)

        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {

        Glide.with(context).load(searchResults[position].userImg).into(holder.userImg)
        holder.userName.text = searchResults[position].userName

        holder.container.setOnClickListener {
            val i = Intent(context, UserProfileActivity::class.java)
            GlobalStaticAdapter.userName2 = holder.userName.text.toString()
            GlobalStaticAdapter.uid2 = searchResults[position].userUid
            GlobalStaticAdapter.key2 = searchResults[position].userKey
            GlobalStaticAdapter.fcmToken2 = searchResults[position].fcm
            GlobalStaticAdapter.imageUrl2 = searchResults[position].userImg

            context.startActivity(i)
        }

    }

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImg: ImageView = itemView.findViewById(R.id.img_search_act)
        val userName:TextView = itemView.findViewById(R.id.tv_search_act)
        val container:CardView = itemView.findViewById(R.id.cv_search_act)
    }
}