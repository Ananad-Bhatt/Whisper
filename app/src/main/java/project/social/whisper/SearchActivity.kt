package project.social.whisper

import adapters.DatabaseAdapter
import adapters.SearchRecyclerViewAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import models.SearchModel
import project.social.whisper.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var b:ActivitySearchBinding
    private var searchJob: Job? = null

    private val searchResults = ArrayList<SearchModel>()
    private lateinit var resultAdapter:SearchRecyclerViewAdapter

    interface OnSearchCompleteListener {
        fun onSearchComplete(results: List<SearchModel>)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(b.root)

        setSupportActionBar(b.searchActToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        b.searchActRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,false)

        b.toolbarSearch.addTextChangedListener {

            //Finding users
            Log.d("DB_ERROR", b.toolbarSearch.text.toString())
            searchResults.clear()
            if(b.toolbarSearch.text.toString() != "") {

                searchJob?.cancel() // Cancel the previous search job if any

                searchJob = lifecycleScope.launch {
                    delay(300)

                    findUser(b.toolbarSearch.text.toString(), object : OnSearchCompleteListener {

                        override fun onSearchComplete(results: List<SearchModel>) {
                            Log.d("DB_ERROR", results.size.toString())
                            resultAdapter =
                                SearchRecyclerViewAdapter(this@SearchActivity, searchResults)
                            b.searchActRv.adapter = resultAdapter

                        }

                    })
                }
            }
        }

    }
    private fun findUser(query: String, listener: OnSearchCompleteListener) {
        searchResults.clear()
        Log.d("DB_ERROR", searchResults.size.toString())
        try {
            DatabaseAdapter.userDetailsTable.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        val key = i.key
                        if(key == DatabaseAdapter.returnUser()?.uid)
                        {
                            continue
                        }

                        val userName: String = if(i.child("USER_NAME").exists()) {
                            i.child("USER_NAME").getValue(String::class.java) ?: ""
                        } else {
                            "guest_"+ key?.substring(0,3)
                        }

                        val image: String = if(i.child("IMAGE").exists()) {
                            i.child("IMAGE").getValue(String::class.java) ?: ""
                        } else {
                            "https://53.fs1.hubspotusercontent-na1.net/hub/53/hubfs/image8-2.jpg?width=595&height=400&name=image8-2.jpg"
                        }

                        if((userName.lowercase()).contains(query.lowercase())) {
                            searchResults.add(SearchModel(userName, image))
                        }
                    }
                    listener.onSearchComplete(searchResults)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("DB_ERROR", error.toString())
                }

            })
        }catch(e:Exception)
        {
            Log.d("DB_ERROR",e.toString())
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