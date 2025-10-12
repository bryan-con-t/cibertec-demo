package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapters.HistorialAdapter
import com.example.myapplication.entity.ListaCompras

class HistorialActivity : AppCompatActivity() {
    private lateinit var rvHistorial : RecyclerView
    private lateinit var historialAdapter : HistorialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_historial)

        rvHistorial = findViewById(R.id.rvCompras)
        rvHistorial.layoutManager = LinearLayoutManager(this)
//        rvHistorial.layoutManager = GridLayoutManager(this, 2)
//        val listacompras = listOf(
//            ListaCompras("Pan", 20, 2, "07/10/2025"),
//            ListaCompras("Leche", 10, 5, "04/10/2025"),
//            ListaCompras("Huevos", 5, 70, "04/10/2025"),
//            ListaCompras("Azúcar", 1, 1, "02/10/2025"),
//            ListaCompras("Queso", 3, 4, "02/10/2025")
//        )
//        historialAdapter = HistorialAdapter(listacompras)
        rvHistorial.adapter = historialAdapter

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