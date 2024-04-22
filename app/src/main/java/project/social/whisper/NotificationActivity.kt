package project.social.whisper

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import adapters.NotificationAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import models.NotificationModel
import project.social.whisper.databinding.ActivityNotificationBinding

class NotificationActivity : BaseActivity() {

    private val notifications = ArrayList<NotificationModel>()
    private val adapter = NotificationAdapter(this, notifications)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.rvNotificationAct.layoutManager = LinearLayoutManager(this)
        b.rvNotificationAct.adapter = adapter

        getNotifications()

    }

    private fun getNotifications() {
        DatabaseAdapter.notificationTable.child(GlobalStaticAdapter.key)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())
                    {
                        for(s in snapshot.children)
                        {
                            DatabaseAdapter.keyUidTable.child(s.key!!)
                                .addListenerForSingleValueEvent(object: ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists())
                                        {
                                            val uid = snapshot
                                                .getValue(String::class.java)!!

                                            DatabaseAdapter.userDetailsTable
                                                .child(uid).child(s.key!!)
                                                .addListenerForSingleValueEvent(object: ValueEventListener {
                                                    override fun onDataChange(snapshot1: DataSnapshot) {
                                                        if (!snapshot1.exists()) {
                                                            val userName: String =
                                                                snapshot1.child("USER_NAME").getValue(String::class.java)!!

                                                            val image: String =
                                                                snapshot1.child("IMAGE").getValue(String::class.java)
                                                                    ?: getString(R.string.image_not_found)

                                                            for(sn in s.children) {
                                                                val msg = sn.child("NOTIFICATION")
                                                                    .getValue(String::class.java)!!

                                                                notifications.add(NotificationModel(image, msg, s.key!!))
                                                                adapter.notifyItemInserted(notifications.size)
                                                            }

                                                        }
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {

                                                    }
                                                })
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                    }

                                })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun getUserDetails(uid: String, key: String) {

    }

    override fun getSelectedTheme(): String {
        val sharedPreferences = getSharedPreferences("app_theme", MODE_PRIVATE)
        return sharedPreferences.getString("theme", "primary1")?: "primary1"
    }

    override fun getWhiteOrBlackTheme(): String {
        val sharedPreferences = getSharedPreferences("app_theme_wb", MODE_PRIVATE)
        return sharedPreferences.getString("theme_wb", "system")?: "system"
    }
}