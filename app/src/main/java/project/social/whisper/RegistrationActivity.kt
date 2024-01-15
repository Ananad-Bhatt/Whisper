package project.social.whisper

import fragments.CreateAccountFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import project.social.whisper.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View binding
        val b = ActivityRegistrationBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        val createAccountFrag = CreateAccountFragment()
        val fm:FragmentManager = supportFragmentManager
        val ft:FragmentTransaction = fm.beginTransaction()
        ft.replace(R.id.fragment_reg,createAccountFrag)
        ft.commit()
    }
}