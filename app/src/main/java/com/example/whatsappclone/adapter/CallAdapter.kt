package com.example.whatsappclone.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappclone.R
import com.example.whatsappclone.model.CallModel

class CallAdapter(private val callList: List<CallModel>) : RecyclerView.Adapter<CallAdapter.ViewHolder>() {

    private var onClickListener: CallAdapter.OnClickListener? = null
    fun setOnClickListener(onClickListener: CallAdapter.OnClickListener) {
        this.onClickListener = onClickListener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val calledUserName: TextView = itemView.findViewById(R.id.called_user_name)
        val calledDateTime: TextView = itemView.findViewById(R.id.called_date_time)
        val calledProfile: ImageView = itemView.findViewById(R.id.callerImage)
        val calledIcon: ImageView = itemView.findViewById(R.id.callType_Icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.call_list_layouts, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val call = callList[position]

        // Bind data to views
        holder.calledUserName.text = call.userName ?: ""
        holder.calledDateTime.text = call.dateTime ?: ""
        if(!call.imageUrl.isNullOrEmpty()){
            Glide.with(holder.itemView.context).load(call.imageUrl.toString()).error(R.drawable.avatar).into(holder.calledProfile)
        }
        if(!call.callType.isNullOrEmpty() && call.callType.equals("Video")){
            holder.calledIcon.setImageResource(R.drawable.outline_videocam_24)
        }
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, call )
            }
        }
    }

    override fun getItemCount(): Int {
        return callList.size
    }

    interface OnClickListener {
        fun onClick(position: Int, individualCaller: CallModel)
    }
}
