package com.example.apprestobarx

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Data class para los items
data class CarruselItem(
    val imagen: Int,
    val titulo: String,
    val subtitulo: String
)

// Constructor del Adapter para que reciba una lista de CarruselItem
class CarruselAdapter(private val items: List<CarruselItem>) : RecyclerView.Adapter<CarruselAdapter.CarruselViewHolder>() {

    inner class CarruselViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgCarrusel: ImageView = view.findViewById(R.id.imgCarrusel)
        // Esto hace referencia a los nuevos TextViews
        val tvTitulo: TextView = view.findViewById(R.id.tvTituloCarrusel)
        val tvSubtitulo: TextView = view.findViewById(R.id.tvSubtituloCarrusel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarruselViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carrusel, parent, false)
        return CarruselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarruselViewHolder, position: Int) {
        val item = items[position]
        holder.imgCarrusel.setImageResource(item.imagen)
        // Asigna los textos al item actual
        holder.tvTitulo.text = item.titulo
        holder.tvSubtitulo.text = item.subtitulo
    }

    override fun getItemCount(): Int = items.size
}