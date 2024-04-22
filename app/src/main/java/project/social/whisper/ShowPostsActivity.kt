package project.social.whisper

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import adapters.ProfileRecyclerViewAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import models.HomeModel
import project.social.whisper.databinding.ActivityShowPostsBinding

class ShowPostsActivity : BaseActivity() {

    private lateinit var b:ActivityShowPostsBinding
    private val posts = ArrayList<HomeModel>()
    private lateinit var adapter: ProfileRecyclerViewAdapter
    private lateinit var userKey:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityShowPostsBinding.inflate(layoutInflater)
        setContentView(b.root)

        val isFromAct = intent.getBooleanExtra("isFromAct", false)

        userKey = if(isFromAct) GlobalStaticAdapter.key2 else GlobalStaticAdapter.key

        if(isFromAct)
            b.tvPostActUserName.text = GlobalStaticAdapter.userName2
        else
            b.tvPostActUserName.text = GlobalStaticAdapter.userName

        b.rvShowAllPosts.layoutManager = GridLayoutManager(this, 3)
        adapter = ProfileRecyclerViewAdapter(posts, applicationContext)
        b.rvShowAllPosts.adapter = adapter

        getAllPosts()
    }

    private fun getAllPosts() {
        try{
            posts.clear()
            DatabaseAdapter.postTable
                .child(userKey)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val post = snapshot.childrenCount

                            for(s in snapshot.children)
                            {
                                val timeStamp = s.key!!

                                val title = s.child("USERNAME").getValue(String::class.java)!!

                                val image = s.child("IMAGE").getValue(String::class.java)
                                    ?: getString(R.string.image_not_found)

                                val cap = s.child("CAPTION").getValue(String::class.java)
                                    ?: "Caption"

                                val score =
                                    s.child("SCORE").getValue(Int::class.java) ?: 0

                                val userImage =
                                    s.child("USER_IMAGE").getValue(String::class.java)
                                        ?: getString(R.string.image_not_found)

                                posts.add(HomeModel(userKey, timeStamp, title, userImage, cap, image, score))
                                adapter.notifyItemInserted(posts.size)
                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })

        }catch(_:Exception){}
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