package com.example.whatsappclone.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import com.example.whatsappclone.R
import com.example.whatsappclone.model.ContactSaved

class ContactAdapter(private val dataList: List<ContactSaved>) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile: CircleImageView = itemView.findViewById(R.id.profileImage)
        val name: TextView = itemView.findViewById(R.id.username)
        val msg: TextView = itemView.findViewById(R.id.user_lastmsg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_display_chatscreen,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val individualUser = dataList[position]
        if(individualUser.dp?.isNotEmpty() == true) {
            Glide.with(holder.itemView.context)
                .load(individualUser.dp).error(R.drawable.avatar)
                .into(holder.profile)
        }
        if(individualUser.firstname?.isNotEmpty() == true) {
            holder.name.text = "${individualUser.firstname} ${individualUser.lastname}"
        }
        else{
            holder.name.text = "${individualUser.phone} (You)"
        }

        if(individualUser.about.equals("")){
            holder.msg.text = ""
        }
        else{
            holder.msg.visibility = View.VISIBLE
            holder.msg.text = individualUser.about
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, individualUser )
            }
        }
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, individualUser: ContactSaved)
    }

}