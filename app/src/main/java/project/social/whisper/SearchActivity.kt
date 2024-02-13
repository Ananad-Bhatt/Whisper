package project.social.whisper

import adapters.SearchRecyclerViewAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import models.HomeModel
import models.SearchModel
import project.social.whisper.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private val searchResults = ArrayList<SearchModel>()
    private val filtered = ArrayList<SearchModel>()
    private lateinit var resultAdapter:SearchRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(b.root)

        setSupportActionBar(b.searchActToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        b.searchActRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,false)

        b.toolbarSearch.addTextChangedListener {



            resultAdapter = SearchRecyclerViewAdapter(filtered)
            b.searchActRv.adapter = resultAdapter
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