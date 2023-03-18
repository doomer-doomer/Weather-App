package com.current.weatherforecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class ForecastAdapter(private val itemz:List<DayforcastComponents>): RecyclerView.Adapter<ForecastAdapter.Holder>() {

    class Holder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imgage : ImageView = itemView.findViewById(R.id.daysforcast)
        val timec: TextView = itemView.findViewById(R.id.daysforcasttime)
        val tempc: TextView = itemView.findViewById(R.id.daysforcasttemp)
        val condz:TextView = itemView.findViewById(R.id.daysforcastcondition)
        val speed :TextView = itemView.findViewById(R.id.daysforcastspeed)
        val dir:TextView = itemView.findViewById(R.id.daysforcastdir)
        val humid:TextView = itemView.findViewById(R.id.daysforcasthumid)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dayforcast,parent,false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = itemz[position]
        holder.imgage.setBackgroundResource(item.img)

        holder.timec.text = item.time
        holder.tempc.text = item.temp
        holder.condz.text = item.cond
        holder.speed.text = item.spd
        holder.dir.text = item.way
        holder.humid.text = item.hum
    }

    override fun getItemCount(): Int {
        return itemz.size
    }
}