package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import models.ProfileSettingModel
import project.social.whisper.R

class ProfileSettingAdapter(private val items: List<ProfileSettingModel>) : RecyclerView.Adapter<ProfileSettingAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.img_profile_setting)
        val textView: TextView = itemView.findViewById(R.id.txt_profile_setting)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_setting_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position].text
        holder.iconImageView.setImageResource(items[position].iconResId)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}