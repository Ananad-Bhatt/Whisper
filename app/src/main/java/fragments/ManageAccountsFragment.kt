package fragments

import adapters.DatabaseAdapter
import adapters.SearchRecyclerViewAdapter
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ManageAccountsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManageAccountsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var b:FragmentManageAccountsBinding

    private val key = DatabaseAdapter.returnUser()?.uid!!

    private val accounts = ArrayList<SearchModel>()

    private lateinit var ad:SearchRecyclerViewAdapter

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

            ad = SearchRecyclerViewAdapter(requireActivity(), accounts)
            b.rvManageAccFrag.adapter = ad
        }

        findAccounts()

        return b.root
    }

    private fun findAccounts() {
        try{
            DatabaseAdapter.userDetailsTable.child(key).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())
                    {
                        for(s in snapshot.children)
                        {
                            val userName = s.key!!

                            val imgUrl = s.child("IMAGE").getValue(String::class.java) ?:
                            "https://53.fs1.hubspotusercontent-na1.net/hub/53/hubfs/image8-2.jpg?width=595&height=400&name=image8-2.jpg"

                            accounts.add(SearchModel(userName, imgUrl))
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ManageAccountsFragment.
         */
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