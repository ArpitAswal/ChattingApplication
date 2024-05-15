package com.example.whatsappclone.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.model.MessagesModel

class GroupChatAdapter(private val dataList: List<MessagesModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ONE = 1
    private val VIEW_TYPE_TWO = 2
    private val map1 = hashMapOf(0 to "x")

    private var listOfMaps = mutableListOf(map1)
    private val mapsToAdd = mutableListOf<HashMap<Int, String>>() // Create a separate list to collect modifications

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ONE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.sender_chat_bubble, parent, false)
                ViewHolderType1(view)
            }

            VIEW_TYPE_TWO -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.group_chat_receiver_bubble, parent, false)
                ViewHolderType2(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    private fun checkNameColor(name: String): Pair<Boolean,Int> {
        for (map in listOfMaps) {
            if (map.containsValue(name)) {
                for ((key, mapValue) in map) {
                    if (mapValue == name) {
                        return Pair(true,key)
                    }
                }
            }
        }
        return Pair(false,0)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]
        val color  = getRandomColor()

        when (holder) {
            is ViewHolderType1 -> {
                // Bind data for ViewHolderType1
                // For example: holder.bind(dataList[position])
                holder.senderMsg.text = item.msg
                holder.senderTime.text = item.time.toString()
            }

            is ViewHolderType2 -> {
                // Bind data for ViewHolderType2
                // For example: holder.bind(dataList[position])
                holder.receiver_msg.text = item.msg
                holder.receiver_time.text = item.time.toString()
                holder.receiver_owner.text = item.owner.toString()

                val contain = checkNameColor(item.owner.toString())
                if(contain.first){
                    holder.receiver_owner.setTextColor(contain.second)
                }
                else {
                    holder.receiver_owner.setTextColor(color)
                    mapsToAdd.add(hashMapOf(color to item.owner.toString())) // Collect modifications in separate list
                }

                listOfMaps.addAll(mapsToAdd)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        // Return view type based on position or data condition

        return if (dataList[position].id.equals(References.getCurrentUserId())) {
            VIEW_TYPE_ONE
        } else
            VIEW_TYPE_TWO
    }

    inner class ViewHolderType1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder for item_layout_type1.xml
        var senderMsg: TextView = itemView.findViewById(R.id.sender_msg_text)
        var senderTime: TextView = itemView.findViewById(R.id.sender_time_text)

    }

    inner class ViewHolderType2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder for item_layout_type2.xml
        val receiver_msg: TextView = itemView.findViewById(R.id.group_receiver_msg_text)
        val receiver_time: TextView = itemView.findViewById(R.id.group_receiver_time_text)
        val receiver_owner: TextView = itemView.findViewById(R.id.group_msg_owner)
    }

    private fun getRandomColor(): Int {
        // Generate random RGB values
        val r = (Math.random() * 256).toInt()
        val g = (Math.random() * 256).toInt()
        val b = (Math.random() * 256).toInt()

        // Combine RGB values into a single color integer
        return Color.rgb(r, g, b)
    }
}