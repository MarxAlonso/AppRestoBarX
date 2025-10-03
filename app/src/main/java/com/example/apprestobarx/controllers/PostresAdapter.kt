package com.example.apprestobarx.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apprestobarx.R
import com.example.apprestobarx.models.Postres

class PostresAdapter(private var listaPostres: List<Postres>) :
    RecyclerView.Adapter<PostresAdapter.PostreViewHolder>() {

    class PostreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPostre: ImageView = itemView.findViewById(R.id.imgPostre)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombrePostre)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecioPostre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_postres, parent, false)
        return PostreViewHolder(view)


    }

    override fun onBindViewHolder(holder: PostreViewHolder, position: Int) {
        val postre = listaPostres[position]

        Glide.with(holder.itemView.context)
            .load(postre.imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.imgPostre)

        holder.tvNombre.text = postre.name
        holder.tvPrecio.text = "S/ ${postre.price}"
    }

    override fun getItemCount(): Int = listaPostres.size

    fun updateList(nuevaLista: List<Postres>) {
        listaPostres = nuevaLista
        notifyDataSetChanged()
    }
}
