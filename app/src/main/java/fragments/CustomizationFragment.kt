package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import project.social.whisper.R
import project.social.whisper.databinding.FragmentCustomizationBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CustomizationFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var b:FragmentCustomizationBinding

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
        b = FragmentCustomizationBinding.inflate(inflater, container, false)

        if(isAdded) {
            val sharedPreferences =
                requireActivity().getSharedPreferences("app_theme", AppCompatActivity.MODE_PRIVATE)

            b.primary1Color.setOnClickListener {
                sharedPreferences.edit().apply {
                    putString("theme","primary1")
                }.apply()
            }

            b.primary2Color.setOnClickListener {
                sharedPreferences.edit().apply {
                    putString("theme","primary2")
                }.apply()
            }
            b.primary3Color.setOnClickListener {
                sharedPreferences.edit().apply {
                    putString("theme","primary3")
                }.apply()
            }
            b.primary4Color.setOnClickListener {
                sharedPreferences.edit().apply {
                    putString("theme","primary4")
                }.apply()
            }
            b.primary5Color.setOnClickListener {
                sharedPreferences.edit().apply {
                    putString("theme","primary5")
                }.apply()
            }
            b.primary6Color.setOnClickListener {
                sharedPreferences.edit().apply {
                    putString("theme","primary6")
                }.apply()
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
         * @return A new instance of fragment CustomizationFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CustomizationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}