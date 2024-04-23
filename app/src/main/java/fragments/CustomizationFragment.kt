package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

            val sharedPreferences2 = requireActivity().getSharedPreferences("app_theme_wb",
                AppCompatActivity.MODE_PRIVATE
            )

            b.primary1Color.setOnClickListener {
                sharedPreferences.edit().apply {
                    putString("theme","primary1")
                }.apply()

                Toast.makeText(requireActivity(), "Restart App to Apply", Toast.LENGTH_LONG)
                    .show()
            }

            b.primary2Color.setOnClickListener {
                sharedPreferences.edit().apply {
                    putString("theme","primary2")
                }.apply()

                Toast.makeText(requireActivity(), "Restart App to Apply", Toast.LENGTH_LONG)
                    .show()
            }
            b.primary3Color.setOnClickListener {
                sharedPreferences.edit().apply {
                    putString("theme","primary3")
                }.apply()

                Toast.makeText(requireActivity(), "Restart App to Apply", Toast.LENGTH_LONG)
                    .show()
            }
            b.primary4Color.setOnClickListener {
                sharedPreferences.edit().apply {
                    putString("theme","primary4")
                }.apply()

                Toast.makeText(requireActivity(), "Restart App to Apply", Toast.LENGTH_LONG)
                    .show()
            }
            b.primary5Color.setOnClickListener {
                sharedPreferences.edit().apply {
                    putString("theme","primary5")
                }.apply()

                Toast.makeText(requireActivity(), "Restart App to Apply", Toast.LENGTH_LONG)
                    .show()
            }
            b.primary6Color.setOnClickListener {
                sharedPreferences.edit().apply {
                    putString("theme","primary6")
                }.apply()

                Toast.makeText(requireActivity(), "Restart App to Apply", Toast.LENGTH_LONG)
                    .show()
            }

            b.whiteColor.setOnClickListener {
                sharedPreferences2.edit().apply{
                    putString("theme_wb", "light")
                }.apply()

                Toast.makeText(requireActivity(), "Restart App to Apply", Toast.LENGTH_LONG)
                    .show()
            }

            b.blackColor.setOnClickListener {
                sharedPreferences2.edit().apply{
                    putString("theme_wb", "dark")
                }.apply()

                Toast.makeText(requireActivity(), "Restart App to Apply", Toast.LENGTH_LONG)
                    .show()

            }

            b.systemDefaultColor.setOnClickListener {
                sharedPreferences2.edit().apply{
                    putString("theme_wb", "system")
                }.apply()

                Toast.makeText(requireActivity(), "Restart App to Apply", Toast.LENGTH_LONG)
                    .show()
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