package fragments

import adapters.DatabaseAdapter
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import project.social.whisper.R
import project.social.whisper.databinding.FragmentProfileBinding

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

    lateinit var b: FragmentProfileBinding
    private val key = DatabaseAdapter.returnUser()?.uid!!

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

        retrieveData()

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

    private fun retrieveData() {
        DatabaseAdapter.userDetailsTable.child(key).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {

                if(isAdded) {
                    if (s.exists()) {
                        val userName = s.child("USER_NAME").getValue(String::class.java)

                        val imgUrl = s.child("IMAGE").getValue(String::class.java)
                            ?: "https://53.fs1.hubspotusercontent-na1.net/hub/53/hubfs/image8-2.jpg?width=595&height=400&name=image8-2.jpg"

                        val about = s.child("ABOUT").getValue(String::class.java) ?: "Nothing"

                        Glide.with(requireContext()).load(imgUrl).into(b.imgProfileUserImage)
                        b.txtProfileUserName.text = userName
                        b.txtProfileAbout.text = about
                        return
                    }

                    Toast.makeText(
                        requireContext(),
                        "Something went horribly wrong!!!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if(isAdded) {
                    Toast.makeText(requireContext(), "We unable to fetch data", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
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

