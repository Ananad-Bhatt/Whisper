package project.social.whisper

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
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
//        val slideLeftIn: Animation = AnimationUtils.loadAnimation(this, R.anim.slide_left_in)
        //val largeToSmall: Animation = AnimationUtils.loadAnimation(this, R.anim.large_to_small)

        //Setting animation to views
       // b.ivLogoSplashAct.startAnimation(largeToSmall)

        //Creating delay for splash screen
//        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is signed in (non-null) and update UI accordingly.
            b.progressBarSplash.visibility = View.VISIBLE
            b.progressBarSplash.progress = 5
            b.tvSplashAct.text = "We are setting up app for you..."
            val currentUser = DatabaseAdapter.returnUser()
            if(currentUser != null)
            {
                //Animate progress bar
                var progressAnimator = ObjectAnimator.ofInt(b.progressBarSplash, "progress", 0, 10)
                progressAnimator.duration = 800
                progressAnimator.interpolator = LinearInterpolator()
                progressAnimator.start()

                b.tvSplashAct.text = "Getting values from Database..."
                //Find user name
                val uid = DatabaseAdapter.returnUser()?.uid!!
                GlobalStaticAdapter.uid = uid
                DatabaseAdapter.userDetailsTable.child(uid).addListenerForSingleValueEvent(object:
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists())
                        {
                            //Animate progress bar
                            progressAnimator = ObjectAnimator.ofInt(b.progressBarSplash, "progress", 10, 20)
                            progressAnimator.duration = 800
                            progressAnimator.interpolator = LinearInterpolator()
                            progressAnimator.start()

                            b.tvSplashAct.text = "Getting values from Database..."
                            for(s in snapshot.children)
                            {
                                //Animate progress bar
                                progressAnimator = ObjectAnimator.ofInt(b.progressBarSplash, "progress", 20, 30)
                                progressAnimator.duration = 800
                                progressAnimator.interpolator = LinearInterpolator()
                                progressAnimator.start()

                                val key = s.key!!

                                //Checks which account was opened before, If not then 1st account opened by default
                                val isOpened = s.child("IS_OPENED").getValue(Boolean::class.java) ?: true

                                //Animate progress bar
                                progressAnimator = ObjectAnimator.ofInt(b.progressBarSplash, "progress", 30, 40)
                                progressAnimator.duration = 800
                                progressAnimator.interpolator = LinearInterpolator()
                                progressAnimator.start()

                                if(isOpened)
                                {
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        GlobalStaticAdapter.key = key

                                        //Animate progress bar
                                        progressAnimator = ObjectAnimator.ofInt(b.progressBarSplash, "progress", 40, 50)
                                        progressAnimator.duration = 800
                                        progressAnimator.interpolator = LinearInterpolator()
                                        progressAnimator.start()

                                        Handler(Looper.getMainLooper()).postDelayed({
                                            b.tvSplashAct.text = "Just few more seconds..."

                                            GlobalStaticAdapter.about =
                                                s.child("ABOUT").getValue(String::class.java) ?: ""

                                            //Animate progress bar
                                            progressAnimator = ObjectAnimator.ofInt(b.progressBarSplash, "progress", 50, 60)
                                            progressAnimator.duration = 800
                                            progressAnimator.interpolator = LinearInterpolator()
                                            progressAnimator.start()

                                            GlobalStaticAdapter.accountType =
                                                s.child("ACCOUNT_TYPE").getValue(String::class.java)
                                                    ?: "PUBLIC"

                                            //Animate progress bar
                                            progressAnimator = ObjectAnimator.ofInt(b.progressBarSplash, "progress", 60, 70)
                                            progressAnimator.duration = 800
                                            progressAnimator.interpolator = LinearInterpolator()
                                            progressAnimator.start()

                                            GlobalStaticAdapter.imageUrl =
                                                s.child("IMAGE").getValue(String::class.java)
                                                    ?: getString(R.string.image_not_found)

                                            //Animate progress bar
                                            progressAnimator = ObjectAnimator.ofInt(b.progressBarSplash, "progress", 70, 80)
                                            progressAnimator.duration = 800
                                            progressAnimator.interpolator = LinearInterpolator()
                                            progressAnimator.start()

                                            GlobalStaticAdapter.userName =
                                                s.child("USER_NAME").getValue(String::class.java)!!

                                            //Animate progress bar
                                            progressAnimator = ObjectAnimator.ofInt(b.progressBarSplash, "progress", 80, 90)
                                            progressAnimator.duration = 800
                                            progressAnimator.interpolator = LinearInterpolator()
                                            progressAnimator.start()

                                            Handler(Looper.getMainLooper()).postDelayed({
                                                b.tvSplashAct.text =
                                                    "Found details for ${GlobalStaticAdapter.userName}..."

                                                NotificationService.generateToken()

                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    b.tvSplashAct.text = "Starting application..."

                                                    //Animate progress bar
                                                    progressAnimator = ObjectAnimator.ofInt(b.progressBarSplash, "progress", 90, 100)
                                                    progressAnimator.duration = 800
                                                    progressAnimator.interpolator = LinearInterpolator()
                                                    progressAnimator.start()

                                                    //Move to diff Activity
                                                    val i = Intent(
                                                        applicationContext,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(i)

                                                    //Finishing activity so, it clears from stack
                                                    finish()
                                                },2000)
                                            }, 2000)
                                        }, 2000)
                                    },2000)

                                    return
                                }
                            }

                        }
                        else{
                            //If user is null then redirects
                            Handler(Looper.getMainLooper()).postDelayed({
                                b.tvSplashAct.text = "Starting application..."
                            },1000)

                            //Animate progress bar
                            progressAnimator = ObjectAnimator.ofInt(b.progressBarSplash, "progress", 10, 100)
                            progressAnimator.duration = 2000
                            progressAnimator.interpolator = LinearInterpolator()
                            progressAnimator.start()

                            val login = Intent(applicationContext, StartUpActivity::class.java)
                            startActivity(login)
                            //Finishing activity so, it clears from stack
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }else {
                //If user is null then redirects
                Handler(Looper.getMainLooper()).postDelayed({
                    b.tvSplashAct.text = "Starting application..."

                    //Animate progress bar
                    val progressAnimator = ObjectAnimator.ofInt(b.progressBarSplash, "progress", 10, 100)
                    progressAnimator.duration = 2000
                    progressAnimator.interpolator = LinearInterpolator()
                    progressAnimator.start()

                    val login = Intent(this, StartUpActivity::class.java)
                    startActivity(login)
                    //Finishing activity so, it clears from stack
                    finish()
                },1000)
            }
//        }, 4000)

    }
}