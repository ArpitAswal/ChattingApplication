package com.example.whatsappclone.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.model.MessagesModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChatAdapter(private val dataList: List<MessagesModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ONE = 1
    private val VIEW_TYPE_TWO = 2

    private var dayDate = ""
    private val selectedMessages = mutableSetOf<MessagesModel>()
    private var onItemClickListener: ((MessagesModel) -> Unit)? = null
    private var onItemLongClickListener: ((Int) -> Unit)? = null  // Listener for item long clicks

    fun setOnItemClickListener(listener: (MessagesModel) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (Int) -> Unit) {
        onItemLongClickListener = listener
    }

    fun getSelectedMessages(): MutableSet<MessagesModel> {
        return selectedMessages
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearSelections() {
        selectedMessages.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ONE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.sender_chat_bubble, parent, false)
                ViewHolderType1(view)
            }

            VIEW_TYPE_TWO -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.receiver_chat_bubble, parent, false)
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
        val formatTime = formattedTime(item.time!!)
        val isSelected = selectedMessages.contains(item)
        val daydateChat = displayChatHeader(item.time!!)

        when (holder) {
            is ViewHolderType1 -> {
                // Bind data for ViewHolderType1
                // For example: holder.bind(dataList[position])
                holder.senderMsg.text = item.msg
                holder.senderTime.text = formatTime
                holder.bind(isSelected)
                if (dayDate != daydateChat) {
                    dayDate = daydateChat
                    holder.senderDayView.visibility = View.VISIBLE
                    holder.senderDayText.text = dayDate
                }
            }

            is ViewHolderType2 -> {
                holder.receiverMsg.text = item.msg
                holder.receiverTime.text = formatTime
                holder.bind(isSelected)
                if (dayDate != daydateChat) {
                    dayDate = daydateChat
                    holder.receiverDayView.visibility = View.VISIBLE
                    holder.receiverDayText.text = dayDate
                }
            }
        }

        holder.itemView.setOnLongClickListener {
            if (selectedMessages.contains(item)) {
                selectedMessages.remove(item)
            } else {
                selectedMessages.add(item)
            }
            notifyItemChanged(position)
            onItemClickListener?.invoke(item)
            true
        }

        // Handle regular click
        holder.itemView.setOnClickListener {
            if (selectedMessages.isNotEmpty()) {
                if (selectedMessages.contains(item)) {
                    selectedMessages.remove(item)
                } else {
                    selectedMessages.add(item)
                }
                notifyItemChanged(position)
                onItemClickListener?.invoke(item)
            }  // Trigger the item click listener
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
        var senderDayView: CardView = itemView.findViewById(R.id.sender_DayDateView)
        var senderDayText: TextView = itemView.findViewById(R.id.sender_DayDateText)
        fun bind(isSelected: Boolean) {
            // existing code for binding data
            itemView.setBackgroundColor(if (isSelected) Color.LTGRAY else Color.TRANSPARENT)
        }

    }

    inner class ViewHolderType2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder for item_layout_type2.xml
        val receiverMsg: TextView = itemView.findViewById(R.id.receiver_msg_text)
        val receiverTime: TextView = itemView.findViewById(R.id.receiver_time_text)
        var receiverDayText: TextView = itemView.findViewById(R.id.receiver_DayDateText)
        var receiverDayView: CardView = itemView.findViewById(R.id.receiver_DayDateView)
        fun bind(isSelected: Boolean) {
            // existing code for binding data
            itemView.setBackgroundColor(if (isSelected) Color.LTGRAY else Color.TRANSPARENT)
        }
    }

    private fun formattedTime(date: String): String {
        val inputFormat =
            SimpleDateFormat("dd MMMM yyyy 'at' HH:mm:ss 'UTC'XXX", Locale.getDefault())
        val format = inputFormat.parse(date)
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return outputFormat.format(format!!)
    }

    private fun parseDate(dateStr: String): Date {
        val formatter =
            SimpleDateFormat("dd MMMM yyyy 'at' HH:mm:ss 'UTC'XXX", Locale.getDefault())
        return formatter.parse(dateStr) ?: Date()
    }

    private fun formatDate(date: Date, pattern: String): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }

    private fun displayChatHeader(time: String): String {
        val currentDate = Calendar.getInstance().time
        val cal = Calendar.getInstance()

        // Find the current week's Monday
        cal.time = currentDate
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val currentWeekMonday = cal.time

        val messageDate = parseDate(time)
        val messageDateStr = formatDate(messageDate, "dd/MM/yyyy")

        val header = when {
            formatDate(currentDate, "dd/MM/yyyy") == messageDateStr -> "Today"
            isYesterday(messageDate, currentDate) -> "Yesterday"
            messageDate >= currentWeekMonday -> {
                val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(messageDate)
                dayOfWeek
            }

            else -> messageDateStr
        }

        return header

    }

    operator fun Date.minus(days: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = this
        cal.add(Calendar.DAY_OF_YEAR, -days)
        return cal.time
    }

    private fun isYesterday(dateToCheck: Date, currentDate: Date): Boolean {
        val cal = Calendar.getInstance()
        cal.time = currentDate
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = cal.time

        // Compare dates without considering time
        return formatDate(dateToCheck, "dd/MM/yyyy") == formatDate(yesterday, "dd/MM/yyyy")
    }
}