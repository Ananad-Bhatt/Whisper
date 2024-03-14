package fragments

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import project.social.whisper.R
import project.social.whisper.databinding.FragmentProfileEditBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileEditFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var b:FragmentProfileEditBinding

    //Activity Result Launcher
    private lateinit var imageCapture: ActivityResultLauncher<Intent>

    private val uid = GlobalStaticAdapter.uid
    private val key = GlobalStaticAdapter.key

    //Permission callback
    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        when (it) {
            true -> {
                Toast.makeText(requireContext(), "Granted", Toast.LENGTH_LONG).show()
            }

            false -> {
                Toast.makeText(requireContext(), "We are unable to access camera and images", Toast.LENGTH_SHORT).show()
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
        b = FragmentProfileEditBinding.inflate(inflater, container, false)

        Glide.with(requireContext()).load(GlobalStaticAdapter.imageUrl).into(b.imgEditProfileUserImage)
        b.edtEditProfileAbout.setText(GlobalStaticAdapter.about)
        b.edtEditProfileUserName.setText(GlobalStaticAdapter.userName)

        imageCapture = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val uri: Uri? = data?.data
                uploadImage(uri)
            } else {
                if(isAdded) {
                    Toast.makeText(requireContext(), "Image selection canceled", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        b.imgBtnEditProfileCamera.setOnClickListener {
            imgClick()
        }

        b.btnEditProfileDone.setOnClickListener {
            if(b.edtEditProfileUserName.text.toString() != "" && b.edtEditProfileAbout.text.toString() != "")
            {
                DatabaseAdapter.userDetailsTable.child(uid).child(key).child("USER_NAME")
                    .setValue(b.edtEditProfileUserName.text.toString())

                DatabaseAdapter.userDetailsTable.child(uid).child(key).child("ABOUT")
                    .setValue(b.edtEditProfileAbout.text.toString())

                val fm1 = requireActivity().supportFragmentManager
                val ft1 = fm1.beginTransaction()
                ft1.replace(R.id.main_container, ProfileFragment())
                ft1.commit()

            }
        }

        return b.root
    }

    private fun uploadImage(uri: Uri?) {
        try {
            if (uri != null) {
                DatabaseAdapter.userImage.child(key).putFile(uri).addOnSuccessListener {

                    DatabaseAdapter.userImage.child(key).downloadUrl.addOnSuccessListener { img ->

                        GlobalStaticAdapter.imageUrl = img.toString()

                        //Update image in profile
                        Glide.with(requireContext()).load(GlobalStaticAdapter.imageUrl)
                            .into(b.imgEditProfileUserImage)

                        DatabaseAdapter.userDetailsTable.child(uid).child(key).child("IMAGE").setValue(img.toString()).addOnSuccessListener {
                            if(isAdded) {
                                Toast.makeText(requireContext(), "Profile image updated successfully", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
            else
            {
                if(isAdded) {
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong, try again",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }catch(e:Exception)
        {
            Log.d("DB_ERROR",e.toString())
        }
    }

    private fun imgClick() {
        if(isAdded) {
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileEditFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileEditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}