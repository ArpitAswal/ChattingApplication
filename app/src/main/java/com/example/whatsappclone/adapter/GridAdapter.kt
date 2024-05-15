package com.example.whatsappclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.whatsappclone.R
import com.example.whatsappclone.model.ContactSaved
import de.hdodenhof.circleimageview.CircleImageView

class GridAdapter(private val context: Context, private val userList: List<ContactSaved>) : BaseAdapter() {

    override fun getCount(): Int {
        return userList.size
    }

    override fun getItem(position: Int): Any {
        return userList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_list_items, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        // Set user image
        val user = userList[position]
        if(user.dp!!.isNotEmpty()){
            Glide.with(view.context).load(user.dp!!).error(R.drawable.avatar).into(viewHolder.imageView)
        }
        viewHolder.nameView.text = "${user.firstname.toString()} ${user.lastname.toString()}"
        return view
    }

    private class ViewHolder(view: View) {
        val imageView: CircleImageView = view.findViewById(R.id.membersProfile)
        val nameView: TextView = view.findViewById(R.id.memberName)
    }
}
