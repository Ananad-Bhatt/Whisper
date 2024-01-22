package adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import fragments.ChatCurrentFragment
import fragments.ChatFragment
import fragments.ChatRequestFragment
import fragments.HomeFollowingFragment
import fragments.HomeNewFeedFragment

class ChatViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if(position == 0)
            ChatCurrentFragment()
        else
            ChatRequestFragment()
    }
}