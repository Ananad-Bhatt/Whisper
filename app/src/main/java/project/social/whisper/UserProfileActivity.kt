package project.social.whisper

import adapters.DatabaseAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import project.social.whisper.databinding.ActivityUserProfileBinding

class UserProfileActivity : AppCompatActivity() {

    private lateinit var b:ActivityUserProfileBinding

    private lateinit var imgUrl:String
    private lateinit var userUid:String
    private lateinit var userKey:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(b.root)

        val userName = intent.getStringExtra("userName")!!
        userUid = intent.getStringExtra("userUid")!!
        userKey = intent.getStringExtra("userKey")!!

        b.txtProfileActUserName.text = userName

        findUser()

        b.btnProfileActMessage.setOnClickListener {
            val i = Intent(this, ChatActivity::class.java)
            i.putExtra("userName",userName)
            i.putExtra("imgUrl",imgUrl)
            i.putExtra("uid",userUid)
            i.putExtra("key",userKey)
            startActivity(i)
        }

    }

    private fun findUser() {
        DatabaseAdapter.userDetailsTable.child(userUid).child(userKey).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {

                imgUrl = s.child("IMAGE").getValue(String::class.java)?:
                "https://53.fs1.hubspotusercontent-na1.net/hub/53/hubfs/image8-2.jpg?width=595&height=400&name=image8-2.jpg"

                val about = s.child("ABOUT").getValue(String::class.java)?:"Nothing"

                Glide.with(applicationContext).load(imgUrl).into(b.imgProfileActUserImage)
                b.txtProfileActAbout.text = about

                return
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"We are unable to fetch data",Toast.LENGTH_LONG).show()
            }
        })
    }
}