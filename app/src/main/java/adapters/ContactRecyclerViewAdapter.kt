package adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ContextUtils.getActivity
import models.ContactModel
import project.social.whisper.ChatActivity
import project.social.whisper.R

class ContactRecyclerViewAdapter(private val context: Context, private val contactList:ArrayList<ContactModel>) :
    RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_recycler_view, parent, false)

        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ContactViewHolder,
        position: Int
    ) {

        holder.name.text = contactList[position].name
        holder.number.text = contactList[position].number

        holder.container.setOnClickListener {
            GlobalStaticAdapter.contactName = contactList[position].name
            GlobalStaticAdapter.contactNumber = contactList[position].number

            val i = Intent(context, ChatActivity::class.java)
            context.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val container: CardView = itemView.findViewById(R.id.cv_contact_rv)
        val name: TextView = itemView.findViewById(R.id.tv_contact_rv_name)
        val number: TextView = itemView.findViewById(R.id.tv_contact_rv_number)
    }
}