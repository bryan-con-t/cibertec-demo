package com.example.myapplication

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.net.toUri
import com.example.myapplication.data.AppDatabaseHelper
import com.example.myapplication.entity.Usuario
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class AccesoActivity : AppCompatActivity() {
    private lateinit var tvRegistro: TextView
    private lateinit var tietCorreo : TextInputEditText
    private lateinit var tietClave : TextInputEditText
    private lateinit var tilCorreo : TextInputLayout
    private lateinit var tilClave : TextInputLayout
    private lateinit var btnAcceso : Button
    private var codigoEnviado = false
    private var verificationId : String? = null
    private lateinit var ivGoogle : ImageView
    private lateinit var googleClient : GoogleSignInClient
    private lateinit var auth : FirebaseAuth

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
        ivGoogle = findViewById(R.id.ivGoogle)

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(this, gso)

        tietCorreo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!codigoEnviado && s?.length == 9) {
                    Toast.makeText(this@AccesoActivity, "Enviando SMS", Toast.LENGTH_SHORT).show()
                    val opciones = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber("+51$s")
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this@AccesoActivity)
                        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            override fun onVerificationCompleted(credential : PhoneAuthCredential) {
                                auth.signInWithCredential(credential)
                                    .addOnSuccessListener {
                                        Toast.makeText(this@AccesoActivity, "Bienvenido de nuevo", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this@AccesoActivity, "Código incorrecto", Toast.LENGTH_SHORT).show()
                                        tietClave.text?.clear()
                                    }
                            }

                            override fun onVerificationFailed(e : FirebaseException) {
                                Log.e("Error", e.message.toString())
                            }

                            override fun onCodeSent(verificationId : String, token : PhoneAuthProvider.ForceResendingToken) {
                                this@AccesoActivity.verificationId = verificationId
                            }
                        })
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(opciones)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {  }
        })

        tietClave.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 6 && verificationId != null) {
                    val credential = PhoneAuthProvider.getCredential(verificationId!!, s.toString())
                    auth.signInWithCredential(credential)
                        .addOnSuccessListener {
                            Toast.makeText(this@AccesoActivity, "Bienvenido de nuevo", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@AccesoActivity, "Código incorrecto", Toast.LENGTH_SHORT).show()
                            tietClave.text?.clear()
                        }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {  }
        })

        btnAcceso.setOnClickListener {
            validarCampos()
        }

        ivGoogle.setOnClickListener {
            startActivityForResult(googleClient.signInIntent, 1001)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        Toast.makeText(this@AccesoActivity, "Bienvenido de nuevo", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@AccesoActivity, InicioActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@AccesoActivity, "Error", Toast.LENGTH_SHORT).show()
                    }
            }
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
