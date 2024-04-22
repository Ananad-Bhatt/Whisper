package project.social.whisper

import adapters.DatabaseAdapter
import adapters.HomeRecyclerViewAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import models.HomeModel
import project.social.whisper.databinding.ActivityIndividualPostBinding

class IndividualPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = ActivityIndividualPostBinding.inflate(layoutInflater)
        setContentView(b.root)

        val posts = ArrayList<HomeModel>()
        val adapter = HomeRecyclerViewAdapter(posts, this)
        b.rvIndividualPostAct.layoutManager = LinearLayoutManager(this)
        b.rvIndividualPostAct.adapter = adapter

        val key = intent.getStringExtra("key")!!
        val position = intent.getIntExtra("position", 0)

        DatabaseAdapter.postTable.child(key).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (sn in snapshot.children) {

                        val timeStamp = sn.key!!

                        val title = sn.child("USERNAME").getValue(String::class.java)!!

                        val image = sn.child("IMAGE").getValue(String::class.java)
                            ?: getString(R.string.image_not_found)

                        val cap = sn.child("CAPTION").getValue(String::class.java)
                            ?: "Caption"

                        val score =
                            sn.child("SCORE").getValue(Int::class.java) ?: 0

                        val userImage =
                            sn.child("USER_IMAGE").getValue(String::class.java)
                                ?: getString(R.string.image_not_found)

                        posts.add(HomeModel(key, timeStamp, title, userImage, cap, image, score))
                        adapter.notifyItemInserted(posts.size)
                    }
                    b.rvIndividualPostAct.scrollToPosition(position)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}