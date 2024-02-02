package fragments

import adapters.DatabaseAdapter
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import project.social.whisper.LoginActivity
import project.social.whisper.RegistrationActivity
import project.social.whisper.databinding.FragmentCreateAccountBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateAccountFragment : Fragment() {

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

        val b = FragmentCreateAccountBinding.inflate(inflater,container, false)

        b.btnRegLogin.setOnClickListener {
            val login = Intent(activity, LoginActivity::class.java)
            startActivity(login)
        }

        //Verify email
        b.btnRegVerify.setOnClickListener {
            if(b.edtRegEmail.text.toString().isEmpty())
            {
                Toast.makeText(context,"Email cannot be empty!",Toast.LENGTH_LONG).show()
                b.edtRegEmail.error = "Enter email"
                b.edtRegEmail.requestFocus()
                return@setOnClickListener
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(b.edtRegEmail.text.toString()).matches()) {
                Toast.makeText(context, "Enter valid email address", Toast.LENGTH_LONG).show()
                b.edtRegEmail.error = "Enter valid email"
                b.edtRegEmail.requestFocus()
                return@setOnClickListener
            }

            if(b.edtRegPassword.text.toString().isEmpty())
            {
                Toast.makeText(context,"Password cannot be empty!",Toast.LENGTH_LONG).show()
                b.edtRegPassword.error = "Enter password"
                b.edtRegPassword.requestFocus()
                return@setOnClickListener
            }

            if(b.edtRegConPassword.text.toString().isEmpty())
            {
                Toast.makeText(context,"Confirm your password",Toast.LENGTH_LONG).show()
                b.edtRegConPassword.error = "Confirm password"
                b.edtRegConPassword.requestFocus()
                return@setOnClickListener
            }

            if(b.edtRegPassword.text.toString().length < 6)
            {
                Toast.makeText(context,"Password length should be more than 6 letters",Toast.LENGTH_LONG).show()
                b.edtRegPassword.error = "Too weak"
                b.edtRegPassword.requestFocus()
                return@setOnClickListener
            }

            if(b.edtRegPassword.text.toString() != b.edtRegConPassword.text.toString())
            {
                Toast.makeText(context,"Confirm password is different",Toast.LENGTH_LONG).show()
                b.edtRegConPassword.error = "Password does not match"
                b.edtRegConPassword.requestFocus()
                return@setOnClickListener
            }

            //If everything perfect
            DatabaseAdapter.signUpWithMail(b.edtRegEmail.text.toString(), b.edtRegPassword.text.toString()) {
                if(it)
                {
                    Toast.makeText(context,"Done : ${DatabaseAdapter.returnUser()?.email}",Toast.LENGTH_LONG)
                        .show()

                    DatabaseAdapter.verifyEmail(DatabaseAdapter.returnUser()?.email) {it1 ->
                        if(it1)
                        {
                            Toast.makeText(context,"Email verification link is sent to your email, Please verify your email"
                                            ,Toast.LENGTH_LONG).show()
                        }
                    }
                }
                else
                {
                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_LONG).show()
                }
            }
        }

        return b.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateAccountFragment.
         */

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateAccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}