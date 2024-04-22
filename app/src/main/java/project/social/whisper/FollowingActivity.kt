package project.social.whisper

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import adapters.SearchRecyclerViewAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import models.SearchModel
import project.social.whisper.databinding.ActivityFollowingBinding
import project.social.whisper.databinding.FragmentFollowingBinding

class FollowingActivity : BaseActivity() {

    private val followings = ArrayList<SearchModel>()
    private lateinit var adapter:SearchRecyclerViewAdapter
    private lateinit var dbPath: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = ActivityFollowingBinding.inflate(layoutInflater)
        setContentView(b.root)

        adapter = SearchRecyclerViewAdapter(this, followings)

        b.rvFollowingAct.layoutManager = LinearLayoutManager(this)
        b.rvFollowingAct.adapter = adapter

        val isFollower = intent.getBooleanExtra("isFollower", true)

        val key = GlobalStaticAdapter.key2

        if(isFollower) {
            dbPath = DatabaseAdapter.followerTable.child(key)
            b.tvFollowAct.text = "Followers"
        }
        else
            dbPath = DatabaseAdapter.followingTable.child(key)

        dbPath.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(s in snapshot.children)
                    {
                        findKeyToUid(s.key!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun findKeyToUid(key: String) {

        DatabaseAdapter.keyUidTable.child(key).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val uid = snapshot.getValue(String::class.java)!!

                    uidToInfo(uid, key)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }

    private fun uidToInfo(uid: String, key:String) {

        DatabaseAdapter.userDetailsTable.child(uid).child(key)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())
                    {
                        val userName: String =
                            snapshot.child("USER_NAME").getValue(String::class.java)!!

                        val image: String =
                            snapshot.child("IMAGE").getValue(String::class.java)
                                ?: getString(R.string.image_not_found)

                        val fcm = snapshot.child("FCM_TOKEN").getValue(String::class.java)
                            ?: ""

                        val about = snapshot.child("ABOUT").getValue(String::class.java)
                            ?: ""

                        val accType = snapshot.child("ACCOUNT_TYPE")
                            .getValue(String::class.java) ?: "PUBLIC"

                        followings.add(SearchModel(userName, image, uid, key, about, fcm, accType))
                        adapter.notifyItemInserted(followings.size)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    override fun getSelectedTheme(): String {
        val sharedPreferences = getSharedPreferences("app_theme", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.getString("theme", "primary1")?: "primary1"
    }

    override fun getWhiteOrBlackTheme(): String {
        val sharedPreferences = getSharedPreferences("app_theme_wb", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.getString("theme_wb", "system")?: "system"
    }
}
