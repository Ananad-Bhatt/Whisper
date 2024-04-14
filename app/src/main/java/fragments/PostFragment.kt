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
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomnavigation.BottomNavigationView
import project.social.whisper.R
import project.social.whisper.databinding.FragmentPostBinding
import java.util.Date

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class PostFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var b:FragmentPostBinding

    //Activity Result Launcher
    private lateinit var imageCapture: ActivityResultLauncher<Intent>

    private lateinit var uri:Uri

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
        b = FragmentPostBinding.inflate(inflater, container, false)


        imageCapture = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                uri = data?.data!!

                b.txtPostFrag.visibility = View.GONE
                b.ivPostFrag.visibility = View.VISIBLE

                Glide.with(requireActivity()).load(uri).into(b.ivPostFrag)
            } else {
                if(isAdded) {
                    Toast.makeText(requireContext(), "Image selection canceled", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        b.txtPostFrag.setOnClickListener {
            imgClick()
        }

        b.btnPostFrag.setOnClickListener {
            if(b.ivPostFrag.isVisible && b.edtCapPostFrag.text.trim().toString().isNotEmpty()) {
                b.progressPostFrag.visibility = View.VISIBLE

                GlobalStaticAdapter.setViewAndChildrenEnabled(b.llRootPostFrag, false)

                uploadImage(uri)
            }else
            {
                if(b.edtCapPostFrag.text.trim().toString().isNotEmpty())
                {
                    if(isAdded) {
                        Toast.makeText(requireActivity(), "Select image first", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                else
                {
                    Toast.makeText(requireActivity(), "Enter caption", Toast.LENGTH_LONG)
                        .show()
                }

            }
        }

        b.btnCancelPostFrag.setOnClickListener {
            b.txtPostFrag.visibility = View.VISIBLE
            b.ivPostFrag.visibility = View.GONE
        }

        return b.root
    }

    private fun uploadImage(uri: Uri) {
        try {

            val currTime = Date().time.toString()

            val cap = b.edtCapPostFrag.text.toString()

            //Upload image into storage
            DatabaseAdapter.postImage.child(GlobalStaticAdapter.uid)
                .child(GlobalStaticAdapter.key).child(currTime)
                .putFile(uri).addOnSuccessListener {

                //Get download URL
                DatabaseAdapter.postImage.child(GlobalStaticAdapter.uid)
                    .child(GlobalStaticAdapter.key).child(currTime)
                    .downloadUrl.addOnSuccessListener { img ->

                        //Image link
                        DatabaseAdapter.postTable
                            .child(GlobalStaticAdapter.uid)
                            .child(GlobalStaticAdapter.key)
                            .child(currTime)
                            .child("IMAGE")
                            .setValue(img.toString())

                        //Score of Post
                        DatabaseAdapter.postTable
                            .child(GlobalStaticAdapter.uid)
                            .child(GlobalStaticAdapter.key)
                            .child(currTime)
                            .child("SCORE")
                            .setValue(0)

                        //Storing username
                        DatabaseAdapter.postTable
                            .child(GlobalStaticAdapter.uid)
                            .child(GlobalStaticAdapter.key)
                            .child(currTime)
                            .child("USERNAME")
                            .setValue(GlobalStaticAdapter.userName)

                        //Storing image of user
                        DatabaseAdapter.postTable
                            .child(GlobalStaticAdapter.uid)
                            .child(GlobalStaticAdapter.key)
                            .child(currTime)
                            .child("USER_IMAGE")
                            .setValue(GlobalStaticAdapter.imageUrl)

                        //Storing caption
                        DatabaseAdapter.postTable
                            .child(GlobalStaticAdapter.uid)
                            .child(GlobalStaticAdapter.key)
                            .child(currTime)
                            .child("CAPTION")
                            .setValue(cap)

                        GlobalStaticAdapter.setViewAndChildrenEnabled(b.llRootPostFrag, true)
                        b.progressPostFrag.visibility = View.GONE

                            if(isAdded) {
                                Toast.makeText(
                                    requireActivity(),
                                    "Post uploaded successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        val fm = requireActivity().supportFragmentManager
                        val ft = fm.beginTransaction()
                        ft.replace(R.id.main_container, HomeFragment())
                        ft.commit()

                        if(isAdded) {
                            val bottomNavigationView =
                                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
                            bottomNavigationView.selectedItemId = R.id.bottom_nav_home
                        }
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

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}