package fragments

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import project.social.whisper.R
import project.social.whisper.StartUpActivity
import project.social.whisper.databinding.FragmentProfileSettingAccountBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileSettingAccountFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var b:FragmentProfileSettingAccountBinding

    private val uid = GlobalStaticAdapter.uid
    private val key = GlobalStaticAdapter.key

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
        b = FragmentProfileSettingAccountBinding.inflate(inflater, container, false)

        when(GlobalStaticAdapter.accountType)
        {
            "PUBLIC" -> b.spProfileAccountSetting.setSelection(0)
            "PRIVATE" -> b.spProfileAccountSetting.setSelection(1)
            "NOT VISIBLE" -> b.spProfileAccountSetting.setSelection(2)
        }

        if(isAdded) {

            b.btnDelAccProfileSetting.setOnClickListener {
                DatabaseAdapter.userDetailsTable.child(GlobalStaticAdapter.uid)
                    .child(GlobalStaticAdapter.key)
                    .removeValue()
                    .addOnCompleteListener {
                        Toast.makeText(
                            requireContext(), "Account Deleted successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                DatabaseAdapter.userDetailsTable.child(GlobalStaticAdapter.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.childrenCount == 1.toLong())
                            {
                                val fm = requireActivity().supportFragmentManager
                                val ft = fm.beginTransaction()
                                ft.replace(R.id.main_container, DeleteAccountFragment())
                                ft.commit()
                            }
                            else{
                                deleteEverything()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })

            }
        }

        try {
            if(isAdded) {
                val ad = ArrayAdapter(
                    requireActivity(), android.R.layout.simple_list_item_1,
                    resources.getStringArray(R.array.account_type_array)
                )

                b.spProfileAccountSetting.adapter = ad
            }
        }catch(e:Exception)
        {
            Log.d("SPINNER",e.toString())
        }

        try {
            b.spProfileAccountSetting.onItemSelectedListener = SpinnerStateChangeListener()
        }catch (e:Exception)
        {
            Log.d("DB_ERROR",e.toString())
        }

        return b.root
    }

    private fun deleteEverything() {
        DatabaseAdapter.postTable.child(GlobalStaticAdapter.uid)
            .child(GlobalStaticAdapter.key)
            .removeValue()

        DatabaseAdapter.chatRooms.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(s in snapshot.children)
                    {
                        if(s.key!!.contains(GlobalStaticAdapter.key))
                        {
                            DatabaseAdapter.chatRooms.child(s.key!!)
                                .removeValue()
                            return
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        DatabaseAdapter.chatTable.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(s in snapshot.children)
                    {
                        if(s.key!!.contains(GlobalStaticAdapter.key))
                        {
                            DatabaseAdapter.chatTable.child(s.key!!)
                                .removeValue()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        DatabaseAdapter.keysTable.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(s in snapshot.children)
                    {
                        if(s.key!!.contains(GlobalStaticAdapter.key))
                        {
                            DatabaseAdapter.keysTable.child(s.key!!)
                                .removeValue()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        DatabaseAdapter.blockTable.child(GlobalStaticAdapter.key)
            .removeValue()

        val i = Intent(requireActivity(), StartUpActivity::class.java)
        requireActivity().startActivity(i)
        requireActivity().finishAffinity()
    }

    inner class SpinnerStateChangeListener : OnItemSelectedListener{
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val type = when(position)
            {
                0 -> "PUBLIC"
                1 -> "PRIVATE"
                2 -> "NOT VISIBLE"
                else -> "PUBLIC"
            }

            try {
                DatabaseAdapter.userDetailsTable.child(uid).child(key).child("ACCOUNT_TYPE")
                    .setValue(type)
            }catch(e:Exception)
            {
                Log.d("DB_ERROR",e.toString())
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileSettingAccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}