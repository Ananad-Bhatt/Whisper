package project.social.whisper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import fragments.HomeFragment
import project.social.whisper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = ActivityMainBinding.inflate(layoutInflater)
        val view = b.root

        setContentView(view)
        
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.main_container, HomeFragment())
        ft.commit()


    }
}