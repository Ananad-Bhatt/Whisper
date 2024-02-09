package fragments

import adapters.ChatRecyclerViewAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import models.ChatModel
import project.social.whisper.R
import project.social.whisper.databinding.FragmentChatCurrentBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatCurrentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatCurrentFragment : Fragment() {
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
        val b = FragmentChatCurrentBinding.inflate(inflater, container, false)

        val users = ArrayList<ChatModel>()

        b.chatCurrentFragRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL ,false)

        users.add(ChatModel("Het",R.mipmap.ic_launcher_round, "Hello"))
        users.add(ChatModel("Het2",R.mipmap.ic_launcher_round, "Hello"))
        users.add(ChatModel("Het3",R.mipmap.ic_launcher_round, "Hello"))
        users.add(ChatModel("Het4",R.mipmap.ic_launcher_round, "Hello"))
        users.add(ChatModel("Het5",R.mipmap.ic_launcher_round, "Hello"))
        users.add(ChatModel("Het6",R.mipmap.ic_launcher_round, "Hello"))
        users.add(ChatModel("Het7",R.mipmap.ic_launcher_round, "Hello"))
        users.add(ChatModel("Het8",R.mipmap.ic_launcher_round, "Hello"))
        users.add(ChatModel("Het9",R.mipmap.ic_launcher_round, "Hello"))
        users.add(ChatModel("Het10",R.mipmap.ic_launcher_round, "Hello"))
        users.add(ChatModel("Het11",R.mipmap.ic_launcher_round, "Hello"))
        users.add(ChatModel("Het12",R.mipmap.ic_launcher_round, "Hello"))
        users.add(ChatModel("Het13",R.mipmap.ic_launcher_round, "Hello"))
        users.add(ChatModel("Het14",R.mipmap.ic_launcher_round, "Hello"))

        val adapter = ChatRecyclerViewAdapter(users)

        b.chatCurrentFragRecyclerView.adapter = adapter

        return b.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatCurrentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatCurrentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}