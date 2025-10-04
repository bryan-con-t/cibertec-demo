package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.entity.Compra

class HistorialAdapter(private val listaHistorial: List<Compra>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    // Enlaza la interfaz item_historial con el ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    // Mostrar datos en la interfaz
    override fun onBindViewHolder(holder: HistorialViewHolder,position: Int) {
        val compra = listaHistorial[position]
        holder.tvProducto.text = compra.producto
        holder.tvCantidad.text = "Cantidad: ${compra.cantidad}"
        holder.tvFecha.text = "Comprado el: ${compra.fecha}"
    }

    // Cantidad de elementos a mostrar
    override fun getItemCount(): Int {
        return listaHistorial.size
    }

    // Define los elementos visuales de la interfaz
    inner class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProducto : TextView = itemView.findViewById(R.id.tvProducto)
        val tvCantidad : TextView = itemView.findViewById(R.id.tvCantidad)
        val tvFecha : TextView = itemView.findViewById(R.id.tvFecha)
    }
}