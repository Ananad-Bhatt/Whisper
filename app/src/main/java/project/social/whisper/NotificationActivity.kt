package project.social.whisper

import adapters.NotificationAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
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

        notifications.add(NotificationModel(R.string.image_not_found, "Whisper_Dev has requested to follow","123"))
        notifications.add(NotificationModel(R.drawable.info, "Someone has up voted","123"))
        notifications.add(NotificationModel(R.drawable.info, "Someone has down voted","123"))
        notifications.add(NotificationModel((R.string.image_not_found), "Whisper_Dev2 has requested to follow","123"))
        adapter.notifyItemInserted(notifications.size)
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