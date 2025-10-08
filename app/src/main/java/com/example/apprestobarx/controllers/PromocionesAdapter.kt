package com.example.apprestobarx.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apprestobarx.R
import com.example.apprestobarx.models.Promociones

class PromocionesAdapter(private var listaPromociones: List<Promociones>) :
    RecyclerView.Adapter<PromocionesAdapter.PromocionViewHolder>() {

    class PromocionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPromo: ImageView = itemView.findViewById(R.id.imgPromocion)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombrePromocion)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecioPromocion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromocionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promocion, parent, false)
        return PromocionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromocionViewHolder, position: Int) {
        val promo = listaPromociones[position]

        Glide.with(holder.itemView.context)
            .load(promo.imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.imgPromo)

        holder.tvNombre.text = promo.name
        holder.tvPrecio.text = "S/ ${promo.price}"
    }

    override fun getItemCount(): Int = listaPromociones.size

    fun updateList(nuevaLista: List<Promociones>) {
        listaPromociones = nuevaLista
        notifyDataSetChanged()
    }
}
