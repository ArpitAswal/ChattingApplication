package com.example.whatsappclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappclone.R
import com.example.whatsappclone.model.StatusModel

class StatusAdapter(private val statusList: List<StatusModel>) : RecyclerView.Adapter<StatusAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusUserName: TextView = itemView.findViewById(R.id.status_user_name)
        val statusTime: TextView = itemView.findViewById(R.id.status_image_time)
        val statusUpload: ImageView = itemView.findViewById(R.id.statusUpload)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.status_listitems, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val status = statusList[position]

        // Bind data to views
        holder.statusUserName.text = status.userName ?: ""
        holder.statusTime.text = status.time ?: ""
        if(!status.imageUrl.isNullOrEmpty()){
            Glide.with(holder.itemView.context).load(status.imageUrl.toString()).error(R.drawable.avatar).into(holder.statusUpload)
        }
    }

    override fun getItemCount(): Int {
        return statusList.size
    }
}
