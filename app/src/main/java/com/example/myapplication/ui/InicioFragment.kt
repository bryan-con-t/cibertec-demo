package com.example.myapplication.ui

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.data.AppDatabaseHelper
import com.example.myapplication.data.DetalleListaDAO
import com.example.myapplication.entity.DetalleLista
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date


class InicioFragment : Fragment(R.layout.fragment_inicio) {
    private lateinit var tietProducto: TextInputEditText
    private lateinit var ivAgregar: ImageView
    private lateinit var lvCompras: ListView
    private lateinit var btnGuardar : Button
    private val listaCompras = mutableListOf<String>() // Lista en memoria
    private lateinit var adapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tietProducto = view.findViewById(R.id.tietProducto)
        ivAgregar = view.findViewById(R.id.ivAgregar)
        lvCompras = view.findViewById(R.id.lvCompras)
        btnGuardar = view.findViewById(R.id.btnGuardar)

        // Inicializar adaptador
        adapter = ArrayAdapter(
            requireContext(), // Activity contenedora
            android.R.layout.simple_list_item_1, // Diseño de cada elemento de lista
            listaCompras // Datos a utilizar
        )
        lvCompras.adapter = adapter // Asignamos adaptador a ListView

        /**
         * Evento: agregar producto a la lista
         */
        ivAgregar.setOnClickListener {
            val producto = tietProducto.text.toString().trim() // Obtenemos el texto ingresado
            if (producto.isNotEmpty()) { // Si NO está vacío
                listaCompras.add(producto) // Agrega el texto a la lista
                adapter.notifyDataSetChanged() // Avisa al adaptador que se agregó un elemento
                tietProducto.text?.clear() // Limpia el contenido de la caja de texto
            } else { // Si está vacío muestra un mensaje al usuario
                Toast.makeText(requireContext(), "Escribe un producto", Toast.LENGTH_SHORT).show()
            }
        }
        /**
         * Evento: clic en producto de la lista
         */
        lvCompras.setOnItemClickListener { _, _, position, _ ->
            val producto = listaCompras[position] // Obtenemos el producto seleccionado
            Toast.makeText( // Arma el mensaje a mostrar
                requireContext(), // Activity actual
                "Seleccionaste: $producto", //Texto a mostrar, $... es concatenación
                Toast.LENGTH_SHORT // Duración del mensaje
            ).show() // Muestra el mensaje
        }

        /**
         * Evento: clic largo para eliminar producto con Dialog Personalizado
         */
        lvCompras.setOnItemLongClickListener { _, _, position, _ ->
            val producto = listaCompras[position]

            // Inflar el layout personalizado
            val dialogView = layoutInflater.inflate(R.layout.dialog_opciones, null)

            val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTitulo)
            val btnEliminar = dialogView.findViewById<Button>(R.id.btnEliminar)
            val btnMarcar = dialogView.findViewById<Button>(R.id.btnMarcar)
            val btnCancelar = dialogView.findViewById<Button>(R.id.btnCancelar)

            tvTitulo.text = "Opciones para $producto"

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            // Acciones de botones
            btnEliminar.setOnClickListener {
                listaCompras.removeAt(position)
                adapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "$producto eliminado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            btnMarcar.setOnClickListener {
                Toast.makeText(requireContext(), "$producto marcado como comprado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            btnCancelar.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
            true // Para indicar que el clic largo fue manejado
        }

        btnGuardar.setOnClickListener {
            if (listaCompras.isEmpty()) {
                Toast.makeText(requireContext(), "No hay productos para guardar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val dbHelper = AppDatabaseHelper(requireContext())
            val db = dbHelper.writableDatabase

            // Crear una lista de compras en BBDD
            val valoresLista = ContentValues().apply {
                put("fecha", SimpleDateFormat("yyyy-MM-dd").format(Date()))
                put("id_usuario", 1) // Usuario de ejemplo (futuro: login)
            }
            val idLista = db.insert("lista_compras", null, valoresLista)

            // Insertar los detalles de la lista de compras en BBDD
            val detalleListaDAO = DetalleListaDAO(requireContext())
            listaCompras.forEach { producto ->
                detalleListaDAO.insertar(
                    DetalleLista(
                        producto = producto,
                        idListaCompras = idLista.toInt()
                    )
                )
            }
            Toast.makeText(requireContext(), "Lista guardada en BBDD (#$idLista)", Toast.LENGTH_SHORT).show()
            db.close()
        }
    }
}


