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
import com.github.dhaval2404.imagepicker.ImagePicker
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

    lateinit var b: FragmentProfileBinding
    private lateinit var key: String

    private var usersDetailsTable = Firebase.database.getReference("USERS_DETAILS")
    private var usersTable = Firebase.database.getReference("USERS")

    //Activity Result Launcher
    private lateinit var imageCapture: ActivityResultLauncher<Intent>

    //Permission callback
    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        when (it) {
            true -> {
                Toast.makeText(requireContext(), "Granted", Toast.LENGTH_LONG).show()
            }

            false -> {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

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

        imageCapture = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data
                val uri: Uri? = data?.data
                b.imgProfileUserImage.setImageURI(uri)
            } else {
                Toast.makeText(requireContext(), "Image selection canceled", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        key = DatabaseAdapter.returnUser()?.uid.toString()
        clicker()
        imgClick()

        return b.root
    }

    private fun imgClick() {
        b.imgProfileUserImage.setOnClickListener {
            val ad = AlertDialog.Builder(requireContext())
            ad.setMessage("Take picture from")
                .setPositiveButton("CAMERA") { _, _ ->
                    requestCameraPermission()

                    if (hasCameraPermission()) {
                        openCamera()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please give permission of camera",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .setNegativeButton("TAKE FROM FOLDER") { _, _ ->
                    requestStoragePermission()

                    if (hasStoragePermission()) {
                        openExplorer()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please give permission of storage",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            ad.create()
            ad.show()
        }
    }

    private fun openCamera() {
        ImagePicker.with(this)
            .cameraOnly()
            .crop() //Crop image(Optional), Check Customization for more option
            .compress(1024) //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            ) //Final image resolution will be less than 1080 x 1080(Optional)
            .createIntent { intent ->
                imageCapture.launch(intent)
            }
    }

    private fun openExplorer() {

        ImagePicker.with(this)
            .galleryOnly()
            .crop() //Crop image(Optional), Check Customization for more option
            .compress(1024) //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            ) //Final image resolution will be less than 1080 x 1080(Optional)
            .createIntent { intent ->
                imageCapture.launch(intent)
            }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES
            )

            if (permission != PackageManager.PERMISSION_GRANTED) {
                permissionsResultCallback.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                Toast.makeText(requireContext(), "IMG granted", Toast.LENGTH_LONG).show()
            }
        } else {
            val permission = ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE
            )

            if (permission != PackageManager.PERMISSION_GRANTED) {
                permissionsResultCallback.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                Toast.makeText(requireContext(), "STORAGE granted", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestCameraPermission() {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.CAMERA
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(android.Manifest.permission.CAMERA)
        } else {
            Toast.makeText(requireContext(), "CAMERA granted", Toast.LENGTH_LONG).show()
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
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

