package com.example.whatsappclone.country

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R

class CountryAdapter(private val dataList: List<CountryData>, private val clickListener: ClickListener) : RecyclerView.Adapter<CountryAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemCard : CardView = itemView.findViewById(R.id.cardviewItem)
        val flag : TextView = itemView.findViewById(R.id.flagTV)
        val name : TextView = itemView.findViewById(R.id.nameTV)
        val code : TextView = itemView.findViewById(R.id.codeTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item, parent , false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
         return dataList.size
     }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items = dataList[position]
        holder.flag.text = items.flag
        holder.name.text = items.name
        holder.code.text = items.code
        holder.itemCard.setOnClickListener {
            Log.i("carditem","${dataList[position]}")
            clickListener.onFollowClicked(items)
        }
    }

    interface ClickListener{
        fun onFollowClicked(country: CountryData)
    }

}