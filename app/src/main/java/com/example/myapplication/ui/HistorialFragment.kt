package com.example.myapplication.ui

import android.app.AlertDialog
import android.database.Cursor
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.HistorialAdapter
import com.example.myapplication.data.AppDatabaseHelper
import com.example.myapplication.entity.DetalleLista
import com.example.myapplication.entity.ListaCompras
import java.text.SimpleDateFormat
import java.util.Locale

class HistorialFragment : Fragment(R.layout.fragment_historial) {

    private lateinit var rvHistorial : RecyclerView
    private lateinit var historialAdapter : HistorialAdapter
    private val listaCompras = mutableListOf<ListaCompras>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvHistorial = view.findViewById(R.id.rvHistorial)
        rvHistorial.layoutManager = LinearLayoutManager(requireContext())
        historialAdapter = HistorialAdapter(listaCompras)
        rvHistorial.adapter = historialAdapter
        historialAdapter.setOnItemClickListener { listaSeleccionada ->
            mostrarDetalleLista(listaSeleccionada.id)
        }
        cargarListas()
    }

    private fun cargarListas() {
        listaCompras.clear()
        val dbHelper = AppDatabaseHelper(requireContext())
        val db = dbHelper.readableDatabase
        val cursor : Cursor = db.rawQuery(
            "SELECT id_lista_compras, fecha, id_usuario FROM lista_compras ORDER BY fecha DESC",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_lista_compras"))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
                val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario"))
                val fechaFormateada = try {
                    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val date = parser.parse(fecha)
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "PE")).format(date!!)
                } catch (e : Exception) {
                    e.printStackTrace()
                    fecha
                }
                listaCompras.add(
                    ListaCompras(
                        id = id,
                        fecha = fechaFormateada,
                        idUsuario = idUsuario
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        historialAdapter.notifyDataSetChanged()
    }

    /**
     * Muestra los detalles asociados a una lista específica en un Dialog
     */
    private fun mostrarDetalleLista(idLista : Int) {
        val dbHelper = AppDatabaseHelper(requireContext())
        val db = dbHelper.readableDatabase
        val detalles = mutableListOf<DetalleLista>()
        val cursor = db.rawQuery(
            "SELECT * FROM detalle_lista WHERE id_lista_compras = ?",
            arrayOf(idLista.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val detalle = DetalleLista(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id_detalle_lista")),
                    producto = cursor.getString(cursor.getColumnIndexOrThrow("producto")),
                    unidadMedida = cursor.getString(cursor.getColumnIndexOrThrow("unidad_medida")),
                    cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                    precioUnitario = cursor.getDouble(cursor.getColumnIndexOrThrow("precio_unitario")),
                    precioPagado = cursor.getDouble(cursor.getColumnIndexOrThrow("precio_pagado")),
                    idListaCompras = cursor.getInt(cursor.getColumnIndexOrThrow("id_lista_compras"))
                )
                detalles.add(detalle)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        if (detalles.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Sin productos")
                .setMessage("Esta lista no tiene productos guardados.")
                .setPositiveButton("Cerrar", null)
                .show()
        } else {
            val detalleTexto = StringBuilder()
            var total = 0.0
            for (item in detalles) {
                detalleTexto.append("• ${item.cantidad} ${item.unidadMedida} de ${item.producto}\n")
                detalleTexto.append("P. Unit.: S/ ${item.precioUnitario} | Pagado: S/ ${item.precioPagado}\n\n")
                total += item.precioPagado
            }
            val mensajeFinal = "$detalleTexto\nTotal pagado: S/ ${String.format("%.2f", total)}"
            AlertDialog.Builder(requireContext())
                .setTitle("Productos de la lista #${String.format("%03d", idLista)}")
                .setMessage(mensajeFinal)
                .setPositiveButton("Cerrar", null)
                .show()
        }
    }
}

