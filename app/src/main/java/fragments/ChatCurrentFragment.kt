package fragments

import adapters.ChatRecyclerViewAdapter
import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import models.ChatRecyclerModel
import models.ChatUserModel
import project.social.whisper.ChatGptActivity
import project.social.whisper.R
import project.social.whisper.databinding.FragmentChatCurrentBinding
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ChatCurrentFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var senderKey = GlobalStaticAdapter.key

    private lateinit var users:ArrayList<ChatRecyclerModel>
    private lateinit var usersKey:ArrayList<ChatUserModel>

    private lateinit var b:FragmentChatCurrentBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // View binding
        b = FragmentChatCurrentBinding.inflate(inflater, container, false)

        users = ArrayList()
        usersKey = ArrayList()

        b.tvChatGptCurrFrag.setOnClickListener {
            if(isAdded) {
                val i = Intent(requireContext(), ChatGptActivity::class.java)
                startActivity(i)
            }
        }

        b.chatCurrentFragRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL ,false)

        lifecycleScope.launch {
            fetchingData()
            fetchingDetails()
        }

        return b.root
    }

    private suspend fun fetchingData() {
        var isSender: Boolean
        var count = 0

        try {
            val snapshot = suspendCoroutine { continuation ->
                DatabaseAdapter.chatRooms.addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        continuation.resume(snapshot)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(Exception(error.toException()))
                    }
                })
            }

            Log.d("IDK","onDataChange")
            if (snapshot.exists()) {
                for(s in snapshot.children)
                {
                    val key = s.key!!

                    if (key.contains(senderKey)) {

                        val user1 = s.child("USER_1").getValue(String::class.java)!!
                        val user1Uid = s.child("USER_1_UID").getValue(String::class.java)!!

                        val user2 = s.child("USER_2").getValue(String::class.java)!!
                        val user2Uid = s.child("USER_2_UID").getValue(String::class.java)!!

                        val isAccepted = s.child("IS_ACCEPTED").getValue(Boolean::class.java)!!
                        val lastMessage = s.child("LAST_MESSAGE").getValue(String::class.java)?:"HI"

                        isSender = user1 == senderKey

                        if(isSender)
                        {
                            val snapshot1 = suspendCoroutine { continuation ->
                                DatabaseAdapter.blockTable.child(user2).child(user1)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot1: DataSnapshot) {
                                            continuation.resume(snapshot1)
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            continuation.resumeWithException(Exception(error.toException()))
                                        }

                                    })
                            }

                            if (!snapshot1.exists()) {
                                Log.d("BLOCK_ERROR", "Hello")
                                usersKey.add(
                                    ChatUserModel(
                                        user2Uid,
                                        user2,
                                        lastMessage
                                    )
                                )
                            }
                        }
                        else{
                            if(isAccepted)
                            {
                                val snapshot2 = suspendCoroutine { continuation ->
                                    DatabaseAdapter.blockTable.child(user1).child(user2)
                                    .addListenerForSingleValueEvent(object: ValueEventListener{
                                            override fun onDataChange(snapshot2: DataSnapshot) {
                                                continuation.resume(snapshot2)
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                continuation.resumeWithException(Exception(error.toException()))
                                            }
                                    })
                                }
                                if(!snapshot2.exists()) {
                                    Log.d("BLOCK_ERROR", "Hello2")
                                    usersKey.add(
                                        ChatUserModel(
                                            user1Uid,
                                            user1,
                                            lastMessage
                                        )
                                    )
                                }
                            }
                        }
                    }
                    count++
                }
            }

        } catch (e:Exception)
        {
            Log.d("DB_ERROR",e.toString())
        }
    }

    private fun fetchingDetails()
    {
        Log.d("IDK","OK")
        try {
            for(k in usersKey) {
                DatabaseAdapter.userDetailsTable.child(k.uid).child(k.key)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                if(isAdded) {
                                    val userName =
                                        snapshot.child("USER_NAME").getValue(String::class.java)!!

                                    val imgUrl =
                                        snapshot.child("IMAGE").getValue(String::class.java)
                                            ?: getString(R.string.image_not_found)

                                    val fcm =
                                        snapshot.child("FCM_TOKEN").getValue(String::class.java)
                                            ?: ""

                                    val type = snapshot.child("ACCOUNT_TYPE")
                                        .getValue(String::class.java) ?: "PUBLIC"

                                    val about = snapshot.child("ABOUT")
                                        .getValue(String::class.java) ?: ""

                                    users.add(
                                        ChatRecyclerModel(
                                            userName,
                                            about,
                                            imgUrl,
                                            k.lastMessage,
                                            k.key,
                                            k.uid,
                                            fcm,
                                            type
                                        )
                                    )
                                    Log.d("IDK", "ADDED")
                                    if (users.size == usersKey.size) {
                                        if (isAdded) {
                                            val adapter =
                                                ChatRecyclerViewAdapter(requireContext(), users)
                                            b.chatCurrentFragRecyclerView.adapter = adapter
                                        }
                                    }
                                }

                            } else {
                                if(isAdded) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Something went wrong",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("DB_ERROR", error.toString())
                        }
                    })
                Log.d("IDK","First round")
            }
        }catch(e:Exception)
        {
            Log.d("DB_ERROR",e.toString())
        }
        Log.d("IDK","user size"+users.size.toString())
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatCurrentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}