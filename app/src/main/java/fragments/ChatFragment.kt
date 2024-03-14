package fragments

import adapters.ChatViewPagerAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import project.social.whisper.R
import project.social.whisper.databinding.FragmentChatBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChatFragment : Fragment() {
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
        // View binding
        val b = FragmentChatBinding.inflate(inflater, container, false)

        val tab = b.chatFragTabLayout
        val vp = b.chatFragViewPager

        val fa = ChatViewPagerAdapter(this)
        vp.adapter = fa

        val labels = arrayOf("Chats", "Requests")

        if(isAdded) {
            tab.setTabTextColors(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.unselected_tab
                ), // Text color for unselected tabs
                ContextCompat.getColor(
                    requireContext(),
                    R.color.selected_tab
                )   // Text color for selected tab
            )

            tab.setSelectedTabIndicatorColor(
                ContextCompat.getColor(requireContext(), R.color.selected_tab)
            )
        }

//        Assigning title to the tabs
        TabLayoutMediator(tab, vp
        ) { tab1, position ->
            tab1.text = labels[position]
        }.attach()

        return b.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}