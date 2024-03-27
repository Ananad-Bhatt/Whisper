package project.social.whisper

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
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

class SearchActivity : BaseActivity() {

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

        //Action bar
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

                    if(snapshot.exists()) {
                        for (j in snapshot.children) {
                            if(j.exists()) {
                                for (i in j.children) {
                                    if(i.exists()) {
                                        //User Authentication UID
                                        val uid = j.key!!

                                        //User Key
                                        val key = i.key!!

                                        //If it is current user
                                        if (uid == GlobalStaticAdapter.uid) {
                                            break
                                        }

                                        //If account is PUBLIC
                                        val type =
                                            i.child("ACCOUNT_TYPE").getValue(String::class.java)
                                                ?: "PUBLIC"

                                        //If account is not visible
                                        if (type == "NOT VISIBLE") {
                                            continue
                                        }

                                        val userName: String =
                                            i.child("USER_NAME").getValue(String::class.java)!!

                                        val image: String =
                                            i.child("IMAGE").getValue(String::class.java)
                                                ?: getString(R.string.image_not_found)

                                        //FCM token
                                        val fcm = i.child("FCM_TOKEN").getValue(String::class.java)
                                            ?: ""

                                        val about = i.child("ABOUT").getValue(String::class.java)
                                            ?: ""

                                        if ((userName.lowercase()).contains(query.lowercase())) {
                                            searchResults.add(
                                                SearchModel(
                                                    userName,
                                                    image,
                                                    uid,
                                                    key,
                                                    about,
                                                    fcm
                                                )
                                            )
                                        }
                                    }
                                }
                            }
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

    override fun getSelectedTheme(): String {
        val sharedPreferences = getSharedPreferences("app_theme", MODE_PRIVATE)
        return sharedPreferences.getString("theme", "primary1")?: "primary1"
    }

    override fun getWhiteOrBlackTheme(): String {
        val sharedPreferences = getSharedPreferences("app_theme_wb", MODE_PRIVATE)
        return sharedPreferences.getString("theme_wb", "system")?: "system"
    }

}