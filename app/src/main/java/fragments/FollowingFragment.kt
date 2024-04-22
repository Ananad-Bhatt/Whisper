package fragments

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import adapters.SearchRecyclerViewAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import models.SearchModel
import project.social.whisper.R
import project.social.whisper.databinding.FragmentFollowingBinding

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FollowingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FollowingFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private val followings = ArrayList<SearchModel>()
    private lateinit var adapter:SearchRecyclerViewAdapter
    private lateinit var dbPath:DatabaseReference

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
        // Inflate the layout for this fragment
        val b = FragmentFollowingBinding.inflate(inflater, container, false)

        if(isAdded) {
            adapter = SearchRecyclerViewAdapter(requireActivity(), followings)

            b.rvFollowingFrag.layoutManager = LinearLayoutManager(requireActivity())
            b.rvFollowingFrag.adapter = adapter

            DatabaseAdapter.followingTable.child(GlobalStaticAdapter.key)
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                       if(snapshot.exists())
                       {
                           for(s in snapshot.children)
                           {
                               findKeyToUid(s.key!!)
                           }
                       }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }


        return b.root
    }

    private fun findKeyToUid(key: String) {

        DatabaseAdapter.keyUidTable.child(key).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val uid = snapshot.getValue(String::class.java)!!

                    uidToInfo(uid, key)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }

    private fun uidToInfo(uid: String, key:String) {

        DatabaseAdapter.userDetailsTable.child(uid).child(key)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())
                    {
                        val userName: String =
                            snapshot.child("USER_NAME").getValue(String::class.java)!!

                        val image: String =
                            snapshot.child("IMAGE").getValue(String::class.java)
                                ?: getString(R.string.image_not_found)

                        val fcm = snapshot.child("FCM_TOKEN").getValue(String::class.java)
                            ?: ""

                        val about = snapshot.child("ABOUT").getValue(String::class.java)
                            ?: ""

                        followings.add(SearchModel(userName, image, uid, key, about, fcm))
                        adapter.notifyItemInserted(followings.size)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FollowingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}