package adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fragments.CustomizationFragment
import fragments.ManageAccountsFragment
import fragments.FeedBackFragment
import fragments.ProfileSettingAccountFragment
import models.ProfileSettingModel
import project.social.whisper.R
import project.social.whisper.StartUpActivity

class ProfileSettingAdapter(val context: FragmentActivity, private val items: List<ProfileSettingModel>) : RecyclerView.Adapter<ProfileSettingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_setting_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position].text
        holder.iconImageView.setImageResource(items[position].iconResId)

        holder.rl.setOnClickListener {

            val fm1 = context.supportFragmentManager
            val ft1 = fm1.beginTransaction()

            when(holder.textView.text)
            {
                "Account" -> {
                    ft1.replace(R.id.main_container, ProfileSettingAccountFragment())
                    ft1.addToBackStack(null)
                    ft1.commit()
                }

                "Manage Accounts" -> {
                    ft1.replace(R.id.main_container, ManageAccountsFragment())
                    ft1.addToBackStack(null)
                    ft1.commit()
                }

                "Feedback" -> {
                    ft1.replace(R.id.main_container, FeedBackFragment())
                    ft1.addToBackStack(null)
                    ft1.commit()
                }

                "Customization" -> {
                    ft1.replace(R.id.main_container, CustomizationFragment())
                    ft1.addToBackStack(null)
                    ft1.commit()
                }

                "Log Out" -> {
                    //Log out
                    
                    DatabaseAdapter.userDetailsTable.child(GlobalStaticAdapter.uid)
                        .addListenerForSingleValueEvent(object:ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {

                                for(s in snapshot.children)
                                {
                                    val key = s.key!!

                                    DatabaseAdapter.userDetailsTable.child(GlobalStaticAdapter.uid)
                                        .child(key).child("FCM_TOKEN")
                                        .removeValue()
                                }
                                FirebaseAuth.getInstance().signOut()
                                val i = Intent(context, StartUpActivity::class.java)
                                context.startActivity(i)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                
                            }

                        })
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.img_profile_setting)
        val textView: TextView = itemView.findViewById(R.id.txt_profile_setting)
        val rl:RelativeLayout = itemView.findViewById(R.id.rl_profile_setting)
    }
}