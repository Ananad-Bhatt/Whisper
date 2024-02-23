package adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import fragments.ProfileSettingAccountFragment
import fragments.ProfileSettingFragment
import models.ProfileSettingModel
import project.social.whisper.R

class ProfileSettingAdapter(val context: FragmentActivity, private val items: List<ProfileSettingModel>) : RecyclerView.Adapter<ProfileSettingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_setting_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position].text
        holder.iconImageView.setImageResource(items[position].iconResId)

        holder.rl.setOnClickListener {
            when(holder.textView.text)
            {
                "Account" -> {
                    val fm1 = context.supportFragmentManager
                    val ft1 = fm1.beginTransaction()
                    ft1.replace(R.id.main_container, ProfileSettingAccountFragment())
                    ft1.addToBackStack(null)
                    ft1.commit()
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