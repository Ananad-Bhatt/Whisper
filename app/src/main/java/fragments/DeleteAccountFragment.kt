package fragments

import adapters.DatabaseAdapter
import adapters.GlobalStaticAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import project.social.whisper.ForgotPasswordActivity
import project.social.whisper.R
import project.social.whisper.StartUpActivity
import project.social.whisper.databinding.FragmentDeleteAccountBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DeleteAccountFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

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
        val b = FragmentDeleteAccountBinding.inflate(inflater, container, false)

        if(isAdded) {
            b.btnDelAccDel.setOnClickListener {
                if (b.edtEmailDelAcc.text.trim().toString().isNotEmpty()
                    && b.edtPassDelAcc.text.trim().toString().isNotEmpty()
                ) {
                    val credentials = EmailAuthProvider.getCredential(
                        b.edtEmailDelAcc.text.toString(),
                        b.edtPassDelAcc.text.toString()
                    )

                    val user = DatabaseAdapter.returnUser()!!

                    user.reauthenticate(credentials)
                        .addOnCompleteListener {
                            user.delete().addOnCompleteListener {
                                Toast.makeText(
                                    requireActivity(),
                                    "Account is deleted",
                                    Toast.LENGTH_LONG
                                )
                                    .show()

                                deleteEverything()

                                val i = Intent(requireActivity(), StartUpActivity::class.java)
                                requireActivity().startActivity(i)
                                requireActivity().finishAffinity()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(
                                requireActivity(),
                                "Something went wrong",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                } else {
                    Toast.makeText(
                        requireActivity(), "Enter email and password to continue", Toast.LENGTH_LONG
                    ).show()
                }
            }

            b.txtResetPassDelAcc.setOnClickListener {
                val i = Intent(requireActivity(), ForgotPasswordActivity::class.java)
                requireActivity().startActivity(i)
            }
        }


        return b.root
    }

    private fun deleteEverything() {
        DatabaseAdapter.postTable.child(GlobalStaticAdapter.uid)
            .child(GlobalStaticAdapter.key)
            .removeValue()

        DatabaseAdapter.chatRooms.addListenerForSingleValueEvent(object: ValueEventListener {
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

        DatabaseAdapter.chatTable.addListenerForSingleValueEvent(object: ValueEventListener {
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

        DatabaseAdapter.keysTable.addListenerForSingleValueEvent(object: ValueEventListener {
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DeleteAccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DeleteAccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}