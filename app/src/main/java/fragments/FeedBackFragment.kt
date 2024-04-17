package fragments

import adapters.DatabaseAdapter
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import project.social.whisper.R
import project.social.whisper.databinding.FragmentFeedbackBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FeedBackFragment : Fragment() {
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
        val b = FragmentFeedbackBinding.inflate(inflater, container, false)

        b.btnSubmitFeedback.setOnClickListener {
            val rating = b.rbFeedbackFrag.rating.toString()
            if(b.edtFeedbackFeedFrag.text.trim().toString().isNotEmpty()
                && rating != "0")
            {
                val key = DatabaseAdapter.feedbackTable.push().key!!

                try {
                    DatabaseAdapter.feedbackTable.child(key).child("RATING").setValue(rating)
                    DatabaseAdapter.feedbackTable.child(key).child("FEEDBACK")
                        .setValue(b.edtFeedbackFeedFrag.text.toString())

                    Toast.makeText(requireActivity(), "Thank you for your feedback!", Toast.LENGTH_LONG)
                        .show()

                }catch (_:Exception){}
            }

            else if(rating == "0")
            {
                if(isAdded) {
                    val builder = AlertDialog.Builder(requireActivity())
                    builder.setTitle("0 RATING")
                    builder.setMessage("Are you sure want to continue with 0 rating?")
                    builder.setCancelable(false)

                    builder.setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    })

                    builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->

                        val key = DatabaseAdapter.feedbackTable.push().key!!

                        try {
                            DatabaseAdapter.feedbackTable.child(key).child("RATING").setValue(rating)
                            DatabaseAdapter.feedbackTable.child(key).child("FEEDBACK")
                                .setValue(b.edtFeedbackFeedFrag.text.toString())

                            Toast.makeText(requireActivity(), "Thank you for your feedback!", Toast.LENGTH_LONG)
                                .show()

                        }catch (_:Exception){}

                        dialogInterface.dismiss()
                    })

                    builder.create()
                    builder.show()
                }
            }

            else{
                Toast.makeText(requireActivity(), "Feedback cannot be empty!", Toast.LENGTH_LONG)
                    .show()
            }
        }

        return b.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FeedBackFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}