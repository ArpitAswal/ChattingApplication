package com.example.whatsappclone.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappclone.model.UserModel
import de.hdodenhof.circleimageview.CircleImageView
import com.example.whatsappclone.R
import com.example.whatsappclone.model.ListType
import com.example.whatsappclone.model.MessagesModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class UserModelAdapter(private val dataList: List<UserModel>) :
    RecyclerView.Adapter<UserModelAdapter.ViewHolder>() {

    private var onItemClickListener: ((UserModel) -> Unit)? = null
    private var onClickListener: OnClickListener? = null
    private var onGroupClickListener: OnGroupClickListener? = null
    private var onItemLongClickListener: ((Int) -> Unit)? = null  // Listener for item long clicks
    private val selectedChatInbox = mutableSetOf<UserModel>()

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun setOnItemLongClickListener(listener: (Int) -> Unit) {
        onItemLongClickListener = listener
    }

    fun setOnGroupClickListener(listener: OnGroupClickListener) {
        this.onGroupClickListener = listener
    }

    fun setOnItemClickListener(listener: (UserModel) -> Unit) {
        onItemClickListener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile: CircleImageView = itemView.findViewById(R.id.profileImage)
        val name: TextView = itemView.findViewById(R.id.username)
        val msg: TextView = itemView.findViewById(R.id.user_lastmsg)
        val chatTime: TextView = itemView.findViewById(R.id.dateTimeTV)
        fun bind(isSelected: Boolean) {
            // existing code for binding data
            itemView.setBackgroundColor(if (isSelected) Color.LTGRAY else Color.TRANSPARENT)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_display_chatscreen, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun getSelectedInbox(): MutableSet<UserModel> {
        return selectedChatInbox
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearSelections() {
        selectedChatInbox.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val individualUser = dataList[position]
        val isSelected = selectedChatInbox.contains(individualUser)
        if (individualUser.profileImg.isNotEmpty()) {
            Glide.with(holder.itemView.context).load(individualUser.profileImg).into(holder.profile)
        }
        holder.name.text = individualUser.username
        holder.msg.visibility = View.VISIBLE
        holder.msg.text = individualUser.userLastMsg
        if (individualUser.userLastMsg.isNotEmpty()) {
            holder.chatTime.text = formattedDate(individualUser.chatTime)
        } else {
            holder.chatTime.text = ""
        }
        holder.itemView.setOnClickListener {
            if (selectedChatInbox.isEmpty()) {
                when (individualUser.source) {
                    ListType.Individual -> if (onClickListener != null) {
                        onClickListener!!.onClick(position, individualUser)
                    }

                    ListType.Group -> if (onGroupClickListener != null) {
                        onGroupClickListener!!.onGroupClick(position, individualUser)
                    }

                    else -> {}
                }
            }
            if (selectedChatInbox.isNotEmpty()) {
                if (selectedChatInbox.contains(individualUser)) {
                    selectedChatInbox.remove(individualUser)
                } else {
                    selectedChatInbox.add(individualUser)
                }
                notifyItemChanged(position)
                onItemClickListener?.invoke(individualUser)
            }
        }

        holder.itemView.setOnLongClickListener {
            if (selectedChatInbox.contains(individualUser)) {
                selectedChatInbox.remove(individualUser)
            } else {
                selectedChatInbox.add(individualUser)
            }
            notifyItemChanged(position)
            onItemClickListener?.invoke(individualUser)
            true
        }

        holder.bind(isSelected)
    }

    interface OnClickListener {
        fun onClick(position: Int, individualUser: UserModel)
    }

    interface OnGroupClickListener {
        fun onGroupClick(position: Int, individualUser: UserModel)
    }

    private fun formattedDate(dateString: String): String? {
        try {
            // Define the input date format
            val inputFormat =
                SimpleDateFormat("dd MMMM yyyy 'at' HH:mm:ss 'UTC'XXX", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            // Parse the input date string
            val date: Date? = inputFormat.parse(dateString)

            // Define the output date format
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // Format the parsed date into the desired format
            return date?.let { outputFormat.format(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}