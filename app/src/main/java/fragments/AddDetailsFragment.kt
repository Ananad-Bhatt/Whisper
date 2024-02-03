package fragments

import adapters.DatabaseAdapter
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import project.social.whisper.MainActivity
import project.social.whisper.R
import project.social.whisper.databinding.FragmentAddDetailsBinding
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var userTable = Firebase.database.getReference("USERS")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun checkUsernameAvailability(username: String, availabilityTextView: TextView) {
        // Check if the username exists in the database
        // Update the visibility of the TextView accordingly

        // Replace "your_username_key" with the key used to store usernames in your database

        val userKey = DatabaseAdapter.returnUser()?.uid

        userTable.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isUsernameExist = false

                for (userSnapshot in snapshot.children) {
                    val dbUserName = userSnapshot.child("USER_NAME").getValue(String::class.java)

                    if (dbUserName != null && dbUserName.equals(username, ignoreCase = true)) {
                        isUsernameExist = true
                        availabilityTextView.text = "User name is already exist"
                        val drawableStart: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.cross)
                        val textColor = Color.parseColor("#FF0000")
                        availabilityTextView.setTextColor(textColor)
                        availabilityTextView.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null)
                        availabilityTextView.visibility = View.VISIBLE
                        break
                    }
                    else
                    {
                        availabilityTextView.text = "User name is available"
                        val drawableStart: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.tick)
                        val textColor = Color.parseColor("#6B9738")
                        availabilityTextView.setTextColor(textColor)
                        availabilityTextView.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null)
                        availabilityTextView.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                availabilityTextView.text = "Error checking username availability"
                availabilityTextView.visibility = View.VISIBLE
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val b = FragmentAddDetailsBinding.inflate(inflater, container, false)
        val userName = b.edtDetUserName.text.toString()
        var isExist = false

        b.txtDetIsAvailable.visibility = View.GONE

        b.edtDetUserName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Not needed for this example
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check for username availability when the user is typing
                val username = s.toString()
                if(username.isNotEmpty()) {
                    checkUsernameAvailability(username, b.txtDetIsAvailable)
                }else
                {
                    b.txtDetIsAvailable.visibility = View.GONE
                }
            }
        })

        b.btnDetCreateAcc.setOnClickListener {
            if(b.txtDetIsAvailable.text.toString() == "User name is already exist"
                || b.edtDetUserName.text.toString() == "" )
            {
                Toast.makeText(context, "Please enter unique user name", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else
            {
                val key = DatabaseAdapter.returnUser()?.uid

                if (key != null) {
                    try {
                        userTable.child(key).child("USER_NAME").setValue(b.edtDetUserName.text.toString().lowercase())
                    }catch(e:Exception)
                    {
                        Log.d("DB_ERROR",e.toString())
                    }

                    val mainAct = Intent(context,MainActivity::class.java)
                    startActivity(mainAct)
                }
            }
        }

        return b.root
    }

//            if(count>0) {
//                try {
//                    //Finding similar user names
//                    isUserNameExist = object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//
//                            // Checking if userName is already exist or not
//                            if (snapshot.exists()) {
//                                for (dataSnapshot: DataSnapshot in snapshot.children) {
//                                    val dbUserName =
//                                        dataSnapshot.child("EMAIL").getValue(String::class.java)
//
//                                    if (dbUserName != null) {
//                                        if (userName.lowercase() == dbUserName) {
//                                            isExist = true
//                                            return
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        override fun onCancelled(databaseError: DatabaseError) {
//                            // This method will be invoked if there is an error in reading data
//                            println("Error: ${databaseError.toException()}")
//                        }
//                    }
//                } catch (e: Exception) {
//                    Log.d("DB_ERROR", e.toString())
//                }
//            }

            //if(userName != "") {
                //userTable.addValueEventListener(isUserNameExist)

            //}




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}