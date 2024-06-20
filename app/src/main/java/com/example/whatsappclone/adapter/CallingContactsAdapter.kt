package com.example.whatsappclone.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import com.example.whatsappclone.R
import com.example.whatsappclone.model.ContactSaved

class CallingContactsAdapter(private val dataList: List<ContactSaved>,  private val onContactClick: (ContactSaved, Int) -> Unit) : RecyclerView.Adapter<CallingContactsAdapter.ViewHolder>() {

    private val selectedContacts = mutableSetOf<ContactSaved>()
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile: CircleImageView = itemView.findViewById(R.id.contactImage)
        val name: TextView = itemView.findViewById(R.id.contact_name)
        val checked: ImageView = itemView.findViewById(R.id.contact_select)

        fun bind(contact: ContactSaved, onContactClick: (ContactSaved, Int) -> Unit, isSelected: Boolean) {
            name.text = "${contact.firstname} ${contact.lastname}"
            checked.visibility = if (isSelected) View.VISIBLE else View.GONE
            if(contact.dp?.isNotEmpty() == true) {
                Glide.with(itemView.context)
                    .load(contact.dp).error(R.drawable.avatar)
                    .into(profile)
            }

            itemView.setOnClickListener {
                onContactClick(contact, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.all_contacts_list,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val individualUser = dataList[position]

        holder.bind(individualUser, onContactClick, selectedContacts.contains(individualUser))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun toggleSelection(contact: ContactSaved) {
        if (selectedContacts.contains(contact)) {
            selectedContacts.remove(contact)
        } else {
            selectedContacts.add(contact)
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearAllSelectContacts(){
        selectedContacts.clear()
        notifyDataSetChanged()
    }
}