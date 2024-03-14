package project.social.whisper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fragments.ChatFragment
import fragments.HomeFragment
import fragments.ProfileFragment
import fragments.ReelFragment
import project.social.whisper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        View binding
        val b = ActivityMainBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

//        To display fragment
        val fm1 = supportFragmentManager
        val ft1 = fm1.beginTransaction()

//        Home fragment
        ft1.replace(R.id.main_container, HomeFragment())
        ft1.commit()

//        Navigating with Bottom navigation bar
        b.bottomNavBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_nav_home -> {
                    val fm = supportFragmentManager
                    val ft = fm.beginTransaction()
                    ft.replace(R.id.main_container, HomeFragment())
                    ft.commit()
                }
                R.id.bottom_nav_chat -> {
                    val fm = supportFragmentManager
                    val ft = fm.beginTransaction()
                    ft.replace(R.id.main_container, ChatFragment())
                    ft.commit()
                }
                R.id.bottom_nav_reel -> {
                    val fm = supportFragmentManager
                    val ft = fm.beginTransaction()
                    ft.replace(R.id.main_container, ReelFragment())
                    ft.commit()
                }
                else -> {
                    val fm = supportFragmentManager
                    val ft = fm.beginTransaction()
                    ft.replace(R.id.main_container, ProfileFragment())
                    ft.commit()
                }
            }
            true
        }
    }
}