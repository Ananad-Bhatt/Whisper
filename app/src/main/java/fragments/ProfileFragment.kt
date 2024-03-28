package fragments

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import project.social.whisper.R
import project.social.whisper.databinding.FragmentProfileBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var b: FragmentProfileBinding

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
            DatabaseAdapter.followerTable.child(GlobalStaticAdapter.key2)
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
            DatabaseAdapter.postTable.child(GlobalStaticAdapter.uid)
                .child(GlobalStaticAdapter.key)
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val post = snapshot.childrenCount

                            b.txtProfileNoOfPosts.text = post.toString()
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

