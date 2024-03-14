package project.social.whisper

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import project.social.whisper.databinding.ActivitySplashBinding
import services.NotificationService

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Binding View
        val b = ActivitySplashBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        //Creating animation variable
        val slideLeftIn: Animation = AnimationUtils.loadAnimation(this, R.anim.slide_left_in)
        val largeToSmall: Animation = AnimationUtils.loadAnimation(this, R.anim.large_to_small)

        //Setting animation to views
        b.splashLogo.startAnimation(slideLeftIn)
        b.appName.startAnimation(largeToSmall)

        //Creating delay for splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is signed in (non-null) and update UI accordingly.
            val currentUser = DatabaseAdapter.returnUser()
            if(currentUser != null)
            {
                //Find user name
                val uid = DatabaseAdapter.returnUser()?.uid!!
                GlobalStaticAdapter.uid = uid
                DatabaseAdapter.userDetailsTable.child(uid).addListenerForSingleValueEvent(object:
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists())
                        {
                            for(s in snapshot.children)
                            {
                                val key = s.key!!

                                //Checks which account was opened before, If not then 1st account opened by default
                                val isOpened = s.child("IS_OPENED").getValue(Boolean::class.java) ?: true
                                if(isOpened)
                                {
                                    GlobalStaticAdapter.key = key

                                    GlobalStaticAdapter.about = s.child("ABOUT").getValue(String::class.java) ?: ""

                                    GlobalStaticAdapter.accountType = s.child("ACCOUNT_TYPE").getValue(String::class.java) ?: "PUBLIC"

                                    GlobalStaticAdapter.imageUrl = s.child("IMAGE").getValue(String::class.java) ?: getString(R.string.image_not_found)

                                    GlobalStaticAdapter.userName = s.child("USER_NAME").getValue(String::class.java)!!

                                    NotificationService.generateToken()

                                    //Move to diff Activity
                                    val i = Intent(applicationContext, MainActivity::class.java)
                                    startActivity(i)

                                    //Finishing activity so, it clears from stack
                                    finish()

                                    return
                                }
                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }else {
                //If user is null then redirects
                val login = Intent(this, StartUpActivity::class.java)
                startActivity(login)
                //Finishing activity so, it clears from stack
                finish()
            }
        }, 4000)

    }
}