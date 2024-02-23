package adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fragments.ManageAccountsFragment
import fragments.ProfileFragment
import models.SearchModel
import project.social.whisper.R
import project.social.whisper.UserProfileActivity

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
            val fm1 = context.supportFragmentManager
            val ft1 = fm1.beginTransaction()
            ft1.replace(R.id.main_container, ProfileFragment())
            context.finishAffinity()
            ft1.commit()
        }

    }

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImg: ImageView = itemView.findViewById(R.id.img_search_act)
        val userName: TextView = itemView.findViewById(R.id.tv_search_act)
        val container: CardView = itemView.findViewById(R.id.cv_search_act)
    }
}
