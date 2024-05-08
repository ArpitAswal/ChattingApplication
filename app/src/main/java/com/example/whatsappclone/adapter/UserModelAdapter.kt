package com.example.whatsappclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappclone.model.UserModel
import de.hdodenhof.circleimageview.CircleImageView
import com.example.whatsappclone.R

class UserModelAdapter(private val dataList: List<UserModel>) : RecyclerView.Adapter<UserModelAdapter.ViewHolder>() {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val individualUser = dataList[position]
        if(individualUser.profileImg.isNotEmpty()) {
            Glide.with(holder.itemView.context).load(individualUser.profileImg).into(holder.profile)
        }
        holder.name.text = individualUser.username
        holder.msg.visibility = View.VISIBLE
        holder.msg.text = individualUser.userLastMsg
    }
}