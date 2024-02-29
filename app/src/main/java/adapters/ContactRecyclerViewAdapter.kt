package adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import models.ContactModel

class ContactRecyclerViewAdapter(context: Context, private val contactList:ArrayList<ContactModel>) :
    RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactRecyclerViewAdapter.ContactViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(
        holder: ContactRecyclerViewAdapter.ContactViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}