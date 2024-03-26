package adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fragments.ProfileFragment
import models.SearchModel
import project.social.whisper.MainActivity
import project.social.whisper.R

class ManageAccountAdapter(private val context: FragmentActivity, private val searchResults:ArrayList<SearchModel>) :
    RecyclerView.Adapter<ManageAccountAdapter.SearchViewHolder>() {

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

            DatabaseAdapter.userDetailsTable.child(GlobalStaticAdapter.uid)
                .child(GlobalStaticAdapter.key)
                .child("IS_OPENED")
                .setValue(false)

            GlobalStaticAdapter.key = searchResults[position].userKey

            DatabaseAdapter.userDetailsTable.child(GlobalStaticAdapter.uid)
                .child(GlobalStaticAdapter.key)
                .child("IS_OPENED")
                .setValue(true)

            GlobalStaticAdapter.userName = searchResults[position].userName
            //GlobalStaticAdapter.about = searchResults[position].about
            GlobalStaticAdapter.imageUrl = searchResults[position].userImg
            GlobalStaticAdapter.about = searchResults[position].about

            val i = Intent(context, MainActivity::class.java)
            context.startActivity(i)

        }

    }

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImg: ImageView = itemView.findViewById(R.id.img_search_act)
        val userName: TextView = itemView.findViewById(R.id.tv_search_act)
        val container: LinearLayout = itemView.findViewById(R.id.ll_main_search_rv)
    }
}
