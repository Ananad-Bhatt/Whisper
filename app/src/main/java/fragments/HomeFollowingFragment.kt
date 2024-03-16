package fragments

import adapters.DatabaseAdapter
import adapters.HomeRecyclerViewAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import models.HomeModel
import project.social.whisper.R
import project.social.whisper.databinding.FragmentHomeFollowingBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFollowingFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

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
        val b = FragmentHomeFollowingBinding.inflate(inflater,container,false)
        val posts = ArrayList<HomeModel>()

        b.homeFollowingFragRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL ,false)
        val adapter = HomeRecyclerViewAdapter(posts)
        b.homeFollowingFragRecyclerView.adapter = adapter

        DatabaseAdapter.postTable.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(s in snapshot.children) {
                        for(sn in s.children) {
                            val title = sn.child("USERNAME").getValue(String::class.java)!!

                            val image = sn.child("IMAGE").getValue(String::class.java)
                                ?: getString(R.string.image_not_found)

                            val score = sn.child("SCORE").getValue(Double::class.java)?:(0).toDouble()

                            val userImage = sn.child("USER_IMAGE").getValue(String::class.java)
                                ?: getString(R.string.image_not_found)

                            posts.add(HomeModel(title, userImage, "Caption", image, score))
                            adapter.notifyItemInserted(posts.size)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
//        posts.add(HomeModel("Username",R.mipmap.ic_launcher,"caption",R.mipmap.ic_launcher,1))
//        posts.add(HomeModel("Username",R.mipmap.ic_launcher,"caption",R.mipmap.ic_launcher,1))
//        posts.add(HomeModel("Username",R.mipmap.ic_launcher,"caption",R.mipmap.ic_launcher,1))
//        posts.add(HomeModel("Username",R.mipmap.ic_launcher,"caption",R.mipmap.ic_launcher,1))
//        posts.add(HomeModel("Username",R.mipmap.ic_launcher,"caption",R.mipmap.ic_launcher,1))
//        posts.add(HomeModel("Username",R.mipmap.ic_launcher,"caption",R.mipmap.ic_launcher,1))
//        posts.add(HomeModel("Username",R.mipmap.ic_launcher,"caption",R.mipmap.ic_launcher,1))
//        posts.add(HomeModel("Username",R.mipmap.ic_launcher,"caption",R.mipmap.ic_launcher,1))
//        posts.add(HomeModel("Username",R.mipmap.ic_launcher,"caption",R.mipmap.ic_launcher,1))
//        posts.add(HomeModel("Username",R.mipmap.ic_launcher,"caption",R.mipmap.ic_launcher,1))
//        posts.add(HomeModel("Username",R.mipmap.ic_launcher,"caption",R.mipmap.ic_launcher,1))
//        posts.add(HomeModel("Username",R.mipmap.ic_launcher,"caption",R.mipmap.ic_launcher,1))
//        posts.add(HomeModel("Username",R.mipmap.ic_launcher,"caption",R.mipmap.ic_launcher,1))
//        posts.add(HomeModel("Username",R.mipmap.ic_launcher,"caption",R.mipmap.ic_launcher,1))

        return b.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFollowingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFollowingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}