package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.net.toUri
import com.example.myapplication.data.AppDatabaseHelper
import com.example.myapplication.entity.Usuario
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccesoActivity : AppCompatActivity() {
    private lateinit var tvRegistro: TextView
    private lateinit var tietCorreo : TextInputEditText
    private lateinit var tietClave : TextInputEditText
    private lateinit var tilCorreo : TextInputLayout
    private lateinit var tilClave : TextInputLayout
    private lateinit var btnAcceso : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_acceso)

        tvRegistro = findViewById(R.id.tvRegistro)
        tietCorreo = findViewById(R.id.tietCorreo)
        tietClave = findViewById(R.id.tietClave)
        tilCorreo = findViewById(R.id.tilCorreo)
        tilClave = findViewById(R.id.tilClave)
        btnAcceso = findViewById(R.id.btnInicio)

        btnAcceso.setOnClickListener {
            validarCampos()
        }

        tvRegistro.setOnClickListener {
            cambioActivity(RegistroActivity::class.java)
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

    fun validarCampos() {
        val correo = tietCorreo.text.toString().trim()
        val clave = tietClave.text.toString().trim()
        var error = false
        if (correo.isEmpty()) {
            tilCorreo.error = "Ingrese un correo"
            error = true
        } else {
            tilCorreo.error = ""
        }
        if (clave.isEmpty()) {
            tilClave.error = "Ingrese contraseña"
            error = true
        } else {
            tilClave.error = ""
        }

        if (error) {
            return
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                val usuarioEncontrado = withContext(Dispatchers.IO) {
                    val dbHelper = AppDatabaseHelper(this@AccesoActivity)
                    val db = dbHelper.readableDatabase
                    val cursor = db.rawQuery(
                        "SELECT * FROM usuario WHERE correo = ? AND clave = ?",
                        arrayOf(correo, clave)
                    )
                    val user = if (cursor.moveToFirst()) {
                        cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario"))
                    } else null
                    cursor.close()
                    db.close()
                    user
                }
                if (usuarioEncontrado != null) {
                    Toast.makeText(this@AccesoActivity, "Bienvenido", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@AccesoActivity, InicioActivity::class.java))
                } else {
                    Toast.makeText(this@AccesoActivity,
                        "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show()
                }
            }
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
