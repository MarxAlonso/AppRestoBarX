package com.example.apprestobarx.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apprestobarx.R
import com.example.apprestobarx.models.Platillo

class PlatilloAdapter(private var listaPlatillos: List<Platillo>) :
    RecyclerView.Adapter<PlatilloAdapter.PlatilloViewHolder>() {

    class PlatilloViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPlatillo: ImageView = itemView.findViewById(R.id.imgPlatillo)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombrePlatillo)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecioPlatillo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatilloViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_platillo, parent, false)
        return PlatilloViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlatilloViewHolder, position: Int) {
        val platillo = listaPlatillos[position]

        holder.tvNombre.text = platillo.name
        holder.tvPrecio.text = "S/ ${platillo.price}"

        Glide.with(holder.itemView.context)
            .load(platillo.imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.imgPlatillo)
    }

    override fun getItemCount(): Int = listaPlatillos.size

    fun updateList(nuevaLista: List<Platillo>) {
        listaPlatillos = nuevaLista
        notifyDataSetChanged()
    }
}
