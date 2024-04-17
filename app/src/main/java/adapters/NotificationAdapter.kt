package adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import models.NotificationModel
import project.social.whisper.R

class NotificationAdapter(private val context: Context, private val notifications:ArrayList<NotificationModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val postNotification = 1
    private val requestNotification = 2

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return when(viewType)
        {
            postNotification -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.notification_recycler_post, parent, false)
                PostNotification(view)
            }

            requestNotification -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.notification_recycler_request, parent, false)
                RequestNotification(view)
            }

            else -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.notification_recycler_post, parent, false)
                PostNotification(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val notify = notifications[position]

        when(holder.javaClass) {

            PostNotification::class.java -> {
                val h = holder as PostNotification

                Glide.with(context).load(notify.image).into(h.img)

                h.msg.text = notify.message
            }

            RequestNotification::class.java -> {
                val h = holder as RequestNotification

                Glide.with(context).load(notify.image.toString()).into(h.img)

                h.msg.text = notify.message


                h.accept.setOnClickListener {

//                    DatabaseAdapter.followingTable.child(notify.key)
//                        .child(GlobalStaticAdapter.key)
//                        .child("FOLLOWING")
//                        .setValue(true)
//
//                    DatabaseAdapter.followerTable.child(GlobalStaticAdapter.key)
//                        .child(notify.key)
//                        .child("FOLLOWER")
//                        .setValue(true)

                    h.accept.text = "ACCEPTED"
                    h.accept.isEnabled = false
                    h.reject.visibility = View.GONE
                }

                h.reject.setOnClickListener {
                    h.reject.text = "DECLINED"
                    h.reject.isEnabled = false
                    h.accept.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (notifications[position].message.lowercase().contains("requested")) {
            requestNotification
        } else {
            postNotification
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    class PostNotification(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.iv_notification_post)
        val msg = itemView.findViewById<TextView>(R.id.tv_notification_post)
    }

    class RequestNotification(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.iv_notification_request)
        val msg = itemView.findViewById<TextView>(R.id.tv_notification_request)
        val accept = itemView.findViewById<Button>(R.id.btn_accept_notification_req)
        val reject = itemView.findViewById<Button>(R.id.btn_reject_notification_req)
    }
}