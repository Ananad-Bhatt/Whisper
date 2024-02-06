package fragments

import adapters.DatabaseAdapter
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import project.social.whisper.R
import project.social.whisper.databinding.FragmentProfileBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var b:FragmentProfileBinding
    private lateinit var key:String

    private var usersDetailsTable = Firebase.database.getReference("USERS_DETAILS")
    private var usersTable = Firebase.database.getReference("USERS")

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

        key = DatabaseAdapter.returnUser()?.uid.toString()
        clicker()

        return b.root
    }

    private fun clicker() {

        b.txtProfileUserName.setOnClickListener {
            val d = Dialog(requireContext())
            d.setContentView(R.layout.temp_layout)

            val edtVal = d.findViewById<EditText>(R.id.edt_temp_value)
            val btnSubmit: Button = d.findViewById(R.id.btn_temp_submit)

            btnSubmit.setOnClickListener {
                b.txtProfileUserName.text = edtVal.text.toString()
                usersTable.child(key).child("USER_NAME").setValue(edtVal.text.toString())
            }

            d.show()
        }

        b.txtProfileAbout.setOnClickListener {
            val d = Dialog(requireContext())
            d.setContentView(R.layout.temp_layout)

            val edtVal = d.findViewById<EditText>(R.id.edt_temp_value)
            val btnSubmit: Button = d.findViewById(R.id.btn_temp_submit)

            btnSubmit.setOnClickListener {
                b.txtProfileAbout.text = edtVal.text.toString()
                usersDetailsTable.child(key).child("ABOUT").setValue(edtVal.text.toString())
            }

            d.show()

        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
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