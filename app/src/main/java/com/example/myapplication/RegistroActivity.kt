package com.example.myapplication

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.AppDatabaseHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class RegistroActivity : AppCompatActivity() {
    private lateinit var tietDni : TextInputEditText
    private lateinit var tilDni : TextInputLayout
    private lateinit var tietApellidoPaterno : TextInputEditText
    private lateinit var tilApellidoPaterno : TextInputLayout
    private lateinit var tietApellidoMaterno : TextInputEditText
    private lateinit var tilApellidoMaterno : TextInputLayout
    private lateinit var tietNombres : TextInputEditText
    private lateinit var tilNombres : TextInputLayout
    private lateinit var tietCelular : TextInputEditText
    private lateinit var tilCelular : TextInputLayout
    private lateinit var rbtMasculino : RadioButton
    private lateinit var rbtFemenino : RadioButton
    private lateinit var rbtNinguno : RadioButton
    private lateinit var tietCorreo : TextInputEditText
    private lateinit var tilCorreo : TextInputLayout
    private lateinit var tietClave : TextInputEditText
    private lateinit var tilClave : TextInputLayout
    private lateinit var chkTyc : CheckBox
    private lateinit var btnGuardar : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        tietDni = findViewById(R.id.tietDni)
        tilDni = findViewById(R.id.tilDni)
        tietApellidoPaterno = findViewById(R.id.tietApellidoPaterno)
        tilApellidoPaterno = findViewById(R.id.tilApellidoPaterno)
        tietApellidoMaterno = findViewById(R.id.tietApellidoMaterno)
        tilApellidoMaterno = findViewById(R.id.tilApellidoMaterno)
        tietNombres = findViewById(R.id.tietNombres)
        tilNombres = findViewById(R.id.tilNombres)
        tietCelular = findViewById(R.id.tietCelular)
        tilCelular = findViewById(R.id.tilCelular)
        rbtMasculino = findViewById(R.id.rbtMasculino)
        rbtFemenino = findViewById(R.id.rbtFemenino)
        rbtNinguno = findViewById(R.id.rbtNinguno)
        tietCorreo = findViewById(R.id.tietCorreo)
        tilCorreo = findViewById(R.id.tilCorreo)
        tietClave = findViewById(R.id.tietClave)
        tilClave = findViewById(R.id.tilClave)
        chkTyc = findViewById(R.id.chkTyc)
        btnGuardar = findViewById(R.id.btnGuardar)

        btnGuardar.setOnClickListener {
            if (chkTyc.isChecked) {
                registrarUsuarioFirebase()
            } else {
                Toast.makeText(this, "Tiene que aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
            }
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

    private fun registrarUsuarioSQLite() {
        var error = false
        var sexo = "N"
        if (tietDni.text.toString().trim().isEmpty()) {
            tilDni.error = "Ingrese DNI"
            error = true
        } else {
            tilDni.error = ""
        }
        if (tietApellidoPaterno.text.toString().trim().isEmpty()) {
            tilApellidoPaterno.error = "Ingrese apellido paterno"
            error = true
        } else {
            tilApellidoPaterno.error = ""
        }
        if (tietApellidoMaterno.text.toString().trim().isEmpty()) {
            tilApellidoMaterno.error = "Ingrese apellido materno"
            error = true
        } else {
            tilApellidoMaterno.error = ""
        }
        if (tietNombres.text.toString().trim().isEmpty()) {
            tilNombres.error = "Ingrese nombres"
            error = true
        } else {
            tilNombres.error = ""
        }
        if (tietCelular.text.toString().trim().isEmpty()) {
            tilCelular.error = "Ingrese celular"
            error = true
        } else {
            tilCelular.error = ""
        }
        if (tietCorreo.text.toString().trim().isEmpty()) {
            tilCorreo.error = "Ingrese correo electrónico"
            error = true
        } else {
            tilCorreo.error = ""
        }
        if (tietClave.text.toString().trim().isEmpty()) {
            tilClave.error = "Ingrese contraseña"
            error = true
        } else {
            tilClave.error = ""
        }
        if (rbtMasculino.isChecked) {
            sexo = "M"
        }
        if (rbtFemenino.isChecked) {
            sexo = "F"
        }
        if (rbtNinguno.isChecked) {
            sexo = "N"
        }
        if (error) return
        CoroutineScope(Dispatchers.Main).launch {
            val resultado = withContext(Dispatchers.IO) {
                val dbHelper = AppDatabaseHelper(this@RegistroActivity)
                val db = dbHelper.writableDatabase
                val valores = ContentValues().apply {
                    put("dni", tietDni.text.toString().trim())
                    put("apellido_paterno", tietApellidoPaterno.text.toString().trim())
                    put("apellido_materno", tietApellidoMaterno.text.toString().trim())
                    put("nombres", tietNombres.text.toString().trim())
                    put("celular", tietCelular.text.toString().trim())
                    put("sexo", sexo)
                    put("correo", tietCorreo.text.toString().trim() + "@cibertec.edu.pe")
                    put("clave", tietClave.text.toString().trim())
                }
                val id = db.insert("usuario", null, valores)
                db.close()
                id
            }
            if (resultado > 0) {
                Toast.makeText(this@RegistroActivity, "Usuario registrado (#${String.format("%03d", resultado)}", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@RegistroActivity, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registrarUsuarioFirebase() {
        var error = false
        var sexo = "N"
        val dni = tietDni.text.toString().trim()
        val apellidoPaterno = tietApellidoPaterno.text.toString().trim()
        val apellidoMaterno = tietApellidoMaterno.text.toString().trim()
        val nombres = tietNombres.text.toString().trim()
        val celular = tietCelular.text.toString().trim()
        val correo = tietCorreo.text.toString().trim()
        val clave = tietClave.text.toString().trim()

        if (dni.isEmpty()) {
            tilDni.error = "Ingrese DNI"
            error = true
        } else {
            tilDni.error = null
        }
        if (apellidoPaterno.isEmpty()) {
            tilApellidoPaterno.error = "Ingrese apellido paterno"
            error = true
        } else {
            tilApellidoPaterno.error = null
        }
        if (apellidoMaterno.isEmpty()) {
            tilApellidoMaterno.error = "Ingrese apellido materno"
            error = true
        } else {
            tilApellidoMaterno.error = null
        }
        if (nombres.isEmpty()) {
            tilNombres.error = "Ingrese nombres"
            error = true
        } else {
            tilNombres.error = null
        }
        if (celular.isEmpty()) {
            tilCelular.error = "Ingrese celular"
            error = true
        } else {
            tilCelular.error = null
        }
        if (correo.isEmpty()) {
            tilCorreo.error = "Ingrese correo"
            error = true
        } else {
            tilCorreo.error = null
        }
        if (clave.isEmpty()) {
            tilClave.error = "Ingrese contraseña"
            error = true
        } else {
            tilClave.error = null
        }
        sexo = when {
            rbtMasculino.isChecked -> "M"
            rbtFemenino.isChecked -> "F"
            else -> "N"
        }
        if (error) {
            return
        }

        // Firebase Auth
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(correo + "@cibertec.edu.pe", clave)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser!!.uid
                    val usuarioMap = mapOf(
                        "dni" to dni,
                        "apellido_paterno" to apellidoPaterno,
                        "apellido_materno" to apellidoMaterno,
                        "nombres" to nombres,
                        "celular" to celular,
                        "sexo" to sexo,
                        "correo" to correo + "@cibertec.edu.pe"
                    )
                    val db = Firebase.database.reference
                    db.child("usuarios").child(uid).setValue(usuarioMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, InicioActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error guardando datos", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Log.e("Error", "${task.exception?.message}")
                }
            }
    }
}