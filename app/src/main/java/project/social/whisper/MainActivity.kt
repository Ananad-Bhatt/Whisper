package project.social.whisper

import adapters.DatabaseAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import fragments.ChatFragment
import fragments.HomeFragment
import fragments.ProfileFragment
import fragments.ReelFragment
import kotlinx.coroutines.launch
import project.social.whisper.databinding.ActivityMainBinding
import services.NotificationService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        View binding
        val b = ActivityMainBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        //lifecycleScope.launch {
            NotificationService.generateToken()
        //}



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