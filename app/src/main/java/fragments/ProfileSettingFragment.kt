package fragments

import adapters.ProfileSettingAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import models.ProfileSettingModel
import project.social.whisper.R
import project.social.whisper.databinding.FragmentProfileSettingBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileSettingFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var b:FragmentProfileSettingBinding

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
        b = FragmentProfileSettingBinding.inflate(inflater, container, false)

        val settings:ArrayList<ProfileSettingModel> = ArrayList()

        if(isAdded) {
            b.rvProfileSettingFragment.layoutManager = LinearLayoutManager(requireContext())

            settings.add(ProfileSettingModel("Account",R.drawable.profile2))
            settings.add(ProfileSettingModel("Manage Accounts",R.drawable.manage_account))
            settings.add(ProfileSettingModel("Manage Channels",R.drawable.profile))
            settings.add(ProfileSettingModel("Log Out",R.drawable.logout))

            b.rvProfileSettingFragment.adapter = ProfileSettingAdapter(requireActivity(),settings)
        }

        return b.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileSettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}