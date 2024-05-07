package com.example.whatsappclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.model.MessagesModel

class ChatAdapter(private val dataList: List<MessagesModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ONE = 1
    private val VIEW_TYPE_TWO = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ONE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.sender_chat_bubble, parent, false)
                ViewHolderType1(view)
            }
            VIEW_TYPE_TWO -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.receiver_chat_bubble, parent, false)
                ViewHolderType2(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]
        when (holder) {
            is ViewHolderType1 -> {
                // Bind data for ViewHolderType1
                // For example: holder.bind(dataList[position])
                holder.sender_msg.text = item.msg
                holder.sender_time.text = item.time.toString()
            }
            is ViewHolderType2 -> {
                // Bind data for ViewHolderType2
                // For example: holder.bind(dataList[position])
                holder.receiver_msg.text = item.msg
                holder.receiver_time.text = item.time.toString()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        // Return view type based on position or data condition
       if(dataList[position].id.equals(References.getCurrentUserId())){
           return VIEW_TYPE_ONE
       }
        else
            return VIEW_TYPE_TWO
    }

    inner class ViewHolderType1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder for item_layout_type1.xml
        val sender_msg: TextView = itemView.findViewById(R.id.sender_msg_text)
        val sender_time: TextView = itemView.findViewById(R.id.sender_time_text)
    }

    inner class ViewHolderType2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder for item_layout_type2.xml
        val receiver_msg: TextView = itemView.findViewById(R.id.receiver_msg_text)
        val receiver_time: TextView = itemView.findViewById(R.id.receiver_time_text)
    }


}