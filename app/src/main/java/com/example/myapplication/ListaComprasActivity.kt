package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class ListaComprasActivity : AppCompatActivity() {
    private lateinit var tietProducto: TextInputEditText
    private lateinit var ivAgregar: ImageView
    private lateinit var lvCompras: ListView
    private lateinit var btnHistorial : Button

    // Lista en memoria
    private val listaCompras = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_compras)

        tietProducto = findViewById(R.id.tietProducto)
        ivAgregar = findViewById(R.id.ivAgregar)
        lvCompras = findViewById(R.id.lvCompras)
        btnHistorial = findViewById(R.id.btnHistorial)

        // Inicializar adaptador
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listaCompras
        )
        lvCompras.adapter = adapter

        // Evento: agregar producto
        ivAgregar.setOnClickListener {
            val producto = tietProducto.text.toString().trim()
            if (producto.isNotEmpty()) {
                listaCompras.add(producto)
                adapter.notifyDataSetChanged()
                tietProducto.text?.clear()
            } else {
                Toast.makeText(this, "Escribe un producto", Toast.LENGTH_SHORT).show()
            }
        }

        // Evento: clic en producto de la lista
        lvCompras.setOnItemClickListener { _, _, position, _ ->
            val producto = listaCompras[position]
            Toast.makeText(this, "Seleccionaste: $producto", Toast.LENGTH_SHORT).show()
        }

        // Evento: clic largo para eliminar producto con Dialog Nativo
//        lvCompras.setOnItemLongClickListener { _, _, position, _ ->
//            val producto = listaCompras[position]
//
//            // Opciones para el menú
//            val opciones = arrayOf("Eliminar", "Marcar como comprado")
//
//            AlertDialog.Builder(this)
//                .setTitle("Opciones para $producto")
//                .setItems(opciones) { _, which ->
//                    when (which) {
//                        0 -> {
//                            listaCompras.removeAt(position)
//                            adapter.notifyDataSetChanged()
//                            Toast.makeText(this, "$producto eliminado", Toast.LENGTH_SHORT).show()
//                        }
//                        1 -> {
//                            Toast.makeText(this, "$producto marcado como comprado", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//                .setNegativeButton("Cancelar", null)
//                .show()
//
//            true // Para indicar que el clic largo fue manejado
//        }

        // Evento: clic largo para eliminar producto con Dialog Personalizado
        lvCompras.setOnItemLongClickListener { _, _, position, _ ->
            val producto = listaCompras[position]

            // Inflar el layout personalizado
            val dialogView = layoutInflater.inflate(R.layout.dialog_opciones, null)

            val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTitulo)
            val btnEliminar = dialogView.findViewById<Button>(R.id.btnEliminar)
            val btnMarcar = dialogView.findViewById<Button>(R.id.btnMarcar)
            val btnCancelar = dialogView.findViewById<Button>(R.id.btnCancelar)

            tvTitulo.text = "Opciones para $producto"

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            // Acciones de botones
            btnEliminar.setOnClickListener {
                listaCompras.removeAt(position)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "$producto eliminado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            btnMarcar.setOnClickListener {
                Toast.makeText(this, "$producto marcado como comprado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            btnCancelar.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
            true // Para indicar que el clic largo fue manejado
        }

        btnHistorial.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }

        // Hace que el teclado del dispositivo no tape a los Views (EditText, TextInputEditText, etc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, imeInsets.bottom)
            )
            insets
        }
    }
}