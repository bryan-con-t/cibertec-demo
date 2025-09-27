package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.HistorialAdapter
import com.example.myapplication.entity.Compra

class HistorialActivity : AppCompatActivity() {
    private lateinit var rvHistorial : RecyclerView
    private lateinit var historialAdapter: HistorialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_historial)

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

        rvHistorial = findViewById<RecyclerView>(R.id.rvHistorial)

        // Datos de prueba
        val compras = listOf(
            Compra("Leche", 2, "27/09/2025"),
            Compra("Pan", 6, "26/09/2025"),
            Compra("Huevos", 12, "25/09/2025")
        )

        historialAdapter = HistorialAdapter(compras)
        rvHistorial.layoutManager = LinearLayoutManager(this)
        rvHistorial.adapter = historialAdapter
    }
}