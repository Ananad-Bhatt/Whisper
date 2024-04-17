package fragments

import adapters.DatabaseAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import project.social.whisper.R
import project.social.whisper.databinding.FragmentReportBugBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReportBugFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportBugFragment : Fragment() {
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
        val b = FragmentReportBugBinding.inflate(inflater, container, false)

        b.btnSubmitBug.setOnClickListener {

            if(b.edtBugReportFrag.text.trim().toString().isNotEmpty()
                && b.edtBugReportMobileFrag.text.trim().toString().isNotEmpty()
                && b.edtBugReportOsFrag.text.trim().toString().isNotEmpty())
            {
                try {
                    val key = DatabaseAdapter.bugTable.push().key!!

                    val ref = DatabaseAdapter.bugTable.child(key)

                    ref.child("BUG").setValue(b.edtBugReportFrag.text.toString())
                    ref.child("MOBILE").setValue(b.edtBugReportMobileFrag.text.toString())
                    ref.child("VERSION").setValue(b.edtBugReportOsFrag.text.toString())
                }catch (_:Exception){}
            }
            else{
                if(isAdded)
                {
                    Toast.makeText(requireActivity(), "Fill all the information", Toast.LENGTH_LONG)
                        .show()
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
         * @return A new instance of fragment ReportBugFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReportBugFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}