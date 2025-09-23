package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ListaComprasActivity : AppCompatActivity() {
    private lateinit var etProducto: EditText
    private lateinit var btnAgregar: Button
    private lateinit var lvCompras: ListView

    // Lista en memoria
    private val listaCompras = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_compras)

        etProducto = findViewById(R.id.etProducto)
        btnAgregar = findViewById(R.id.btnAgregar)
        lvCompras = findViewById(R.id.lvCompras)

        // Inicializar adaptador
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listaCompras
        )
        lvCompras.adapter = adapter

        // Evento: agregar producto
        btnAgregar.setOnClickListener {
            val producto = etProducto.text.toString().trim()
            if (producto.isNotEmpty()) {
                listaCompras.add(producto)
                adapter.notifyDataSetChanged()
                etProducto.text.clear()
            } else {
                Toast.makeText(this, "Escribe un producto", Toast.LENGTH_SHORT).show()
            }
        }

        // Evento: clic en producto de la lista
        lvCompras.setOnItemClickListener { _, _, position, _ ->
            val producto = listaCompras[position]
            Toast.makeText(this, "Seleccionaste: $producto", Toast.LENGTH_SHORT).show()
        }

        // Evento: clic largo para eliminar producto
        lvCompras.setOnItemLongClickListener { _, _, position, _ ->
            val producto = listaCompras[position]

            // Opciones para el menú
            val opciones = arrayOf("Ver detalles", "Eliminar", "Marcar como comprado")

            AlertDialog.Builder(this)
                .setTitle("Opciones para $producto")
                .setItems(opciones) { _, which ->
                    when (which) {
                        0 -> {
                            Toast.makeText(this, "Detalles de $producto", Toast.LENGTH_SHORT).show()
                        }
                        1 -> {
                            listaCompras.removeAt(position)
                            adapter.notifyDataSetChanged()
                            Toast.makeText(this, "$producto eliminado", Toast.LENGTH_SHORT).show()
                        }
                        2 -> {
                            Toast.makeText(this, "$producto marcado como comprado", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()

            true // Para indicar que el clic largo fue manejado
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}