package project.social.whisper

import adapters.GlobalStaticAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import project.social.whisper.databinding.ActivityUserProfileBinding

class UserProfileActivity : AppCompatActivity() {

    private lateinit var b:ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.txtProfileActUserName.text = GlobalStaticAdapter.userName2
        Glide.with(applicationContext).load(GlobalStaticAdapter.imageUrl2).into(b.imgProfileActUserImage)
        b.txtProfileActAbout.text = GlobalStaticAdapter.about2

        b.btnProfileActMessage.setOnClickListener {
            val i = Intent(this, ChatActivity::class.java)
            startActivity(i)
        }

    }
}