package com.example.whatsappclone.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappclone.R
import com.example.whatsappclone.model.StatusModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class StatusAdapter(private val statusList: List<StatusModel>) :
    RecyclerView.Adapter<StatusAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusUserName: TextView = itemView.findViewById(R.id.status_user_name)
        val statusTime: TextView = itemView.findViewById(R.id.status_image_time)
        val statusUpload: ImageView = itemView.findViewById(R.id.statusUpload)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.status_listitems, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val status = statusList[position]
        // Bind data to views
        val formatTime = formattedTime(status.time!!.toDate())
        holder.statusUserName.text = status.userName ?: ""
        holder.statusTime.text = formatTime
        if (!status.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context).load(status.imageUrl.toString())
                .error(R.drawable.avatar).into(holder.statusUpload)
        }
    }

    override fun getItemCount(): Int {
        return statusList.size
    }

    private fun formattedTime(date: Date): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Format for AM/PM
        return sdf.format(date)
    }

}
