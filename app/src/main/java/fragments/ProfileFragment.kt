package fragments

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import adapters.ProfileRecyclerViewAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import models.HomeModel
import project.social.whisper.R
import project.social.whisper.databinding.FragmentProfileBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var b: FragmentProfileBinding
    val posts = ArrayList<HomeModel>()
    private lateinit var adapter: ProfileRecyclerViewAdapter

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
        b = FragmentProfileBinding.inflate(inflater, container, false)

        //Set user values
        Glide.with(requireContext()).load(GlobalStaticAdapter.imageUrl).into(b.imgProfileUserImage)
        b.txtProfileUserName.text = GlobalStaticAdapter.userName
        b.txtProfileAbout.text = GlobalStaticAdapter.about

        if(isAdded) {
            b.rvProfileRecentPosts.layoutManager = GridLayoutManager(requireActivity(), 3)
            adapter = ProfileRecyclerViewAdapter(posts, requireActivity())
            b.rvProfileRecentPosts.adapter = adapter
        }

        getPostCount()
        getFollowerCount()
        getFollowingCount()

        b.imgBtnProfileEdit.setOnClickListener {
            val fm1 = requireActivity().supportFragmentManager
            val ft1 = fm1.beginTransaction()
            ft1.replace(R.id.main_container, ProfileEditFragment())
            ft1.addToBackStack(null)
            ft1.commit()
        }

        b.imgBtnProfileSetting.setOnClickListener {
            val fm1 = requireActivity().supportFragmentManager
            val ft1 = fm1.beginTransaction()
            ft1.replace(R.id.main_container, ProfileSettingFragment())
            ft1.addToBackStack(null)
            ft1.commit()
        }

        b.linearProfileFollowing.setOnClickListener {

            val args = Bundle()
            args.putBoolean("isFollower", false)

            val frag = FollowingFragment()
            frag.arguments = args

            val fm1 = requireActivity().supportFragmentManager
            val ft1 = fm1.beginTransaction()
            ft1.replace(R.id.main_container, frag)
            ft1.addToBackStack(null)
            ft1.commit()
        }

        b.linearProfileFollowers.setOnClickListener {
            val args = Bundle()
            args.putBoolean("isFollower", true)

            val frag = FollowingFragment()
            frag.arguments = args

            val fm1 = requireActivity().supportFragmentManager
            val ft1 = fm1.beginTransaction()
            ft1.replace(R.id.main_container, frag)
            ft1.addToBackStack(null)
            ft1.commit()
        }

        return b.root
    }

    private fun getFollowingCount() {
        try{
            DatabaseAdapter.followingTable.child(GlobalStaticAdapter.key)
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists())
                        {
                            val follower = snapshot.childrenCount

                            b.txtProfileNoOfFollowing.text = follower.toString()

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }catch (_:Exception){}
    }

    private fun getFollowerCount() {

        try{
            DatabaseAdapter.followerTable.child(GlobalStaticAdapter.key)
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists())
                        {
                            val follower = snapshot.childrenCount

                            b.txtProfileNoOfFollowers.text = follower.toString()

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }catch (_:Exception){}

    }

    private fun getPostCount() {
        try{
            posts.clear()
            DatabaseAdapter.postTable
                .child(GlobalStaticAdapter.key)
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val post = snapshot.childrenCount

                            b.txtProfileNoOfPosts.text = post.toString()

                            for(s in snapshot.children)
                            {
                                val timeStamp = s.key!!

                                val title = s.child("USERNAME").getValue(String::class.java)!!

                                val image = s.child("IMAGE").getValue(String::class.java)
                                    ?: getString(R.string.image_not_found)

                                val cap = s.child("CAPTION").getValue(String::class.java)
                                    ?: "Caption"

                                val score =
                                    s.child("SCORE").getValue(Int::class.java) ?: 0

                                val userImage =
                                    s.child("USER_IMAGE").getValue(String::class.java)
                                        ?: getString(R.string.image_not_found)

                                posts.add(HomeModel(GlobalStaticAdapter.key, timeStamp, title, userImage, cap, image, score))
                                adapter.notifyItemInserted(posts.size)
                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })

        }catch(_:Exception){}
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

