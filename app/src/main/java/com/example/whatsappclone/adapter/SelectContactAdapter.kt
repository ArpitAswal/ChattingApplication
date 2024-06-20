package com.example.whatsappclone.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import com.example.whatsappclone.R
import com.example.whatsappclone.model.ContactSaved

class SelectContactAdapter(
    private var selectedContacts: MutableList<ContactSaved>,
    private val onClearClick: (ContactSaved) -> Unit
) : RecyclerView.Adapter<SelectContactAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile: CircleImageView = itemView.findViewById(R.id.select_contact_profile)
        val name: TextView = itemView.findViewById(R.id.remove_username)
        val clear: FrameLayout = itemView.findViewById(R.id.remove_layout)
        fun bind(contact: ContactSaved, onClearClick: (ContactSaved) -> Unit) {

            if (contact.dp?.isNotEmpty() == true) {
                Glide.with(itemView.context)
                    .load(contact.dp).error(R.drawable.avatar)
                    .into(profile)
            }
            name.text = contact.firstname

            clear.setOnClickListener {
                onClearClick(contact)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_contact_select, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return selectedContacts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val individualUser = selectedContacts[position]
        holder.bind(individualUser, onClearClick)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(contact: ContactSaved) {
        if (selectedContacts.contains(contact)) {
            selectedContacts.remove(contact)
        }
        notifyDataSetChanged()
    }

}