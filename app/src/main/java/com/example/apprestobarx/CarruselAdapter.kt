package com.example.apprestobarx

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CarruselAdapter(
    private val imagenes: List<Int>,
    private val textos: List<String>
) : RecyclerView.Adapter<CarruselAdapter.CarruselViewHolder>() {

    inner class CarruselViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgCarrusel: ImageView = view.findViewById(R.id.imgCarrusel)
        val tvTextoCarrusel: TextView = view.findViewById(R.id.tvTextoCarrusel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarruselViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carrusel, parent, false)
        return CarruselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarruselViewHolder, position: Int) {
        holder.imgCarrusel.setImageResource(imagenes[position])
        holder.tvTextoCarrusel.text = textos[position]
    }

    override fun getItemCount(): Int = imagenes.size
}