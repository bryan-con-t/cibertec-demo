package com.example.myapplication.adapters

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.AppDatabaseHelper
import com.example.myapplication.entity.ListaCompras
import java.text.SimpleDateFormat
import java.util.Locale

class HistorialAdapter(var listaCompras : List<ListaCompras>) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {
    private var onItemClickListener : ((ListaCompras) -> Unit)? = null

    fun setOnItemClickListener(listener : (ListaCompras) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): HistorialViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder : HistorialViewHolder, position : Int) {
        val lista : ListaCompras = listaCompras[position]
        holder.tvIdLista.text = "Lista #${String.format("%03d", lista.id)}"
        val fechaFormateada = try {
            val parser = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = parser.parse(lista.fecha)
            SimpleDateFormat("dd MMM yyy", Locale("es", "PE")).format(date!!)
        } catch (e : Exception) {
            e.printStackTrace()
            lista.fecha
        }
        holder.tvFecha.text = "Fecha: $fechaFormateada"
        val subtotal = obtenerSubtotal(holder.itemView, lista.id)
        holder.tvSubtotal.text = "Total: S/ ${String.format("%.2f", subtotal)}"
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(lista)
        }
    }

    override fun getItemCount() : Int {
        return listaCompras.size
    }

    inner class HistorialViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var tvIdLista : TextView = itemView.findViewById(R.id.tvIdLista)
        var tvSubtotal : TextView = itemView.findViewById(R.id.tvSubtotal)
        var tvFecha : TextView = itemView.findViewById(R.id.tvFecha)
        var ivMenu : ImageView = itemView.findViewById(R.id.ivMenu)
    }

    /**
     * Calcula la suma de precio_pagado de los productos en detalle_lista
     */
    private fun obtenerSubtotal(view : View, idLista : Int) : Double {
        val dbHelper = AppDatabaseHelper(view.context)
        val db = dbHelper.readableDatabase
        var total = 0.0
        val cursor : Cursor = db.rawQuery(
            "SELECT SUM(precio_pagado) as 'subtotal' FROM detalle_lista WHERE id_lista_compras = ?",
            arrayOf(idLista.toString())
        )
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal"))
        }
        cursor.close()
        db.close()
        return total
    }
}

