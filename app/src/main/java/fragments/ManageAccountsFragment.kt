package fragments

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import adapters.ManageAccountAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import models.SearchModel
import project.social.whisper.R
import project.social.whisper.databinding.FragmentManageAccountsBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ManageAccountsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var b:FragmentManageAccountsBinding

    private val uid = GlobalStaticAdapter.uid
    private var key = GlobalStaticAdapter.key

    private val accounts = ArrayList<SearchModel>()

    private lateinit var ad:ManageAccountAdapter

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
        b = FragmentManageAccountsBinding.inflate(inflater, container, false)

        if(isAdded) {
            b.rvManageAccFrag.layoutManager = LinearLayoutManager(requireActivity(),
                LinearLayoutManager.VERTICAL, false)

            ad = ManageAccountAdapter(requireActivity(), accounts)
            b.rvManageAccFrag.adapter = ad
        }

        b.btnManageAccFrag.setOnClickListener {
            key =
                DatabaseAdapter.userDetailsTable
                .child(uid)
                .push().key!!

            if(isAdded) {
                val fm1 = requireActivity().supportFragmentManager
                val ft1 = fm1.beginTransaction()
                ft1.replace(R.id.main_container, ProfileFragment())
                ft1.commit()
            }
        }

        //Finding accounts of 1 email
        findAccounts()

        return b.root
    }

    private fun findAccounts() {
        try{
            DatabaseAdapter.userDetailsTable.child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())
                    {
                        for(s in snapshot.children) {
                            val userKey = s.key!!

                            val userName = s.child("USER_NAME").getValue(String::class.java)!!

                            val imgUrl = s.child("IMAGE").getValue(String::class.java)
                                ?: "https://53.fs1.hubspotusercontent-na1.net/hub/53/hubfs/image8-2.jpg?width=595&height=400&name=image8-2.jpg"

                            val fcm = s.child("FCM_TOKEN").getValue(String::class.java)?:""

                            accounts.add(SearchModel(userName, imgUrl, uid, userKey, fcm))
                            ad.notifyItemInserted(accounts.size)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("DB_ERROR",error.toString())
                }
            })
        }catch(e:Exception)
        {
            Log.d("DB_ERROR",e.toString())
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ManageAccountsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}