package com.current.weatherforecast

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val itemz:List<ItemHolder>): RecyclerView.Adapter<Adapter.Holder>() {

    class Holder(itemView:View):RecyclerView.ViewHolder(itemView){
        val imgage :ImageView = itemView.findViewById(R.id.imagez)
        val text:TextView = itemView.findViewById(R.id.weatherdata)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_cards,parent,false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = itemz[position]
        holder.imgage.setBackgroundResource(item.img)
        holder.text.text = item.par
    }

    override fun getItemCount(): Int {
        return itemz.size
    }
}