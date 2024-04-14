package project.social.whisper

import adapters.DatabaseAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fragments.ChatFragment
import fragments.HomeFragment
import fragments.PostFragment
import fragments.ProfileFragment
import fragments.ReelFragment
import project.social.whisper.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        View binding
        val b = ActivityMainBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

//        DatabaseAdapter.chatTable.child("-NtrIvBSa2C_WL4zUbN2-Nu-5PwNXGS7kTRJNbCr")
//            .child("-Nu2fuGKmaTHT5RKW5t0")
//            .removeValue()

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
                    ft.replace(R.id.main_container, PostFragment())
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

    override fun getSelectedTheme(): String {
        val sharedPreferences = getSharedPreferences("app_theme", MODE_PRIVATE)
        return sharedPreferences.getString("theme", "primary1")?: "primary1"
    }

    override fun getWhiteOrBlackTheme(): String {
        val sharedPreferences = getSharedPreferences("app_theme_wb", MODE_PRIVATE)
        return sharedPreferences.getString("theme_wb", "system")?: "system"
    }
}