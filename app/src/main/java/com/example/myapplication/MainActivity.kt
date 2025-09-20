package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {
    var tvRegistro: TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        tvRegistro = findViewById(R.id.tvRegistro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvRegistro?.setOnClickListener {
            cambioActivity(MainActivity2::class.java)
        }
    }

    fun cambioActivity(activityDestino : Class<out Activity>) {
        val intent = Intent(this, activityDestino)
        startActivity(intent)
    }

    fun abrirVentanaNavegador() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData("http://www.google.com".toUri())
        startActivity(intent)
    }

    fun abrirBuscadorWeb() {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.setData("http://www.google.com".toUri())
        startActivity(intent)
    }

    fun abrirLlamada() {
        val intent = Intent(Intent.ACTION_DIAL)
        startActivity(intent)
    }

    fun llamar() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.setData("tel:+51999999999".toUri())
        startActivity(intent)
    }
}
