package com.example.apprestobarx.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apprestobarx.R
import com.example.apprestobarx.models.Bebidas

class BebidasAdapter(private var listaBebidas: List<Bebidas>) :
    RecyclerView.Adapter<BebidasAdapter.BebidaViewHolder>() {

    class BebidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBebida: ImageView = itemView.findViewById(R.id.imgBebida)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreBebida)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecioBebida)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BebidaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bebida, parent, false) // usamos el item_bebida.xml
        return BebidaViewHolder(view)
    }

    override fun onBindViewHolder(holder: BebidaViewHolder, position: Int) {
        val bebida = listaBebidas[position]

        Glide.with(holder.itemView.context)
            .load(bebida.imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.imgBebida)

        holder.tvNombre.text = bebida.name
        holder.tvPrecio.text = "S/ ${bebida.price}"
    }

    override fun getItemCount(): Int = listaBebidas.size

    fun updateList(nuevaLista: List<Bebidas>) {
        listaBebidas = nuevaLista
        notifyDataSetChanged()
    }
}
