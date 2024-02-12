package project.social.whisper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import project.social.whisper.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(b.root)

        setSupportActionBar(b.searchActToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        b.toolbarSearch.addTextChangedListener {
            Toast.makeText(this, b.toolbarSearch.text.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}