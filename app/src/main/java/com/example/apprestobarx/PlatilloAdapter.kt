package com.example.apprestobarx

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlatilloAdapter(private val listaPlatillos: List<Platillo>) :
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
        holder.imgPlatillo.setImageResource(platillo.imagen)
        holder.tvNombre.text = platillo.nombre
        holder.tvPrecio.text = platillo.precio
    }

    override fun getItemCount(): Int = listaPlatillos.size
}
