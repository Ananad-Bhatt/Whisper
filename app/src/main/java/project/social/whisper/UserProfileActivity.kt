package project.social.whisper

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import adapters.HomeRecyclerViewAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import models.HomeModel
import project.social.whisper.databinding.ActivityUserProfileBinding

class UserProfileActivity : AppCompatActivity() {

    private lateinit var b:ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(b.root)

        val posts = ArrayList<HomeModel>()

        b.txtProfileActUserName.text = GlobalStaticAdapter.userName2
        Glide.with(applicationContext).load(GlobalStaticAdapter.imageUrl2).into(b.imgProfileActUserImage)
        b.txtProfileActAbout.text = GlobalStaticAdapter.about2

        b.btnProfileActMessage.setOnClickListener {
            val i = Intent(this, ChatActivity::class.java)
            startActivity(i)
        }

        b.rvProfileActRecentPosts.layoutManager = GridLayoutManager(this, 2)
        val adapter = HomeRecyclerViewAdapter(posts, applicationContext)
        b.rvProfileActRecentPosts.adapter = adapter

        DatabaseAdapter.postTable.child(GlobalStaticAdapter.uid)
            .child(GlobalStaticAdapter.key)
            .addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                     for(sn in snapshot.children) {
                         val title = sn.child("USERNAME").getValue(String::class.java)!!

                         val image = sn.child("IMAGE").getValue(String::class.java)
                             ?: getString(R.string.image_not_found)

                         val score = sn.child("SCORE").getValue(Int::class.java) ?: 0

                         val userImage = sn.child("USER_IMAGE").getValue(String::class.java)
                             ?: getString(R.string.image_not_found)

                         posts.add(HomeModel(title, userImage, "Caption", image, score))
                         adapter.notifyItemInserted(posts.size)

                     }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}