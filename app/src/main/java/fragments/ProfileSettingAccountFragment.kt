package fragments

import adapters.DatabaseAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import project.social.whisper.R
import project.social.whisper.databinding.FragmentProfileSettingAccountBinding

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileSettingAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileSettingAccountFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var b:FragmentProfileSettingAccountBinding

    private val uid = DatabaseAdapter.returnUser()?.uid!!
    private val key = DatabaseAdapter.key

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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileSettingAccountFragment.
         */
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