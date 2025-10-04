package com.example.myapplication

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.ui.EstadisticasFragment
import com.example.myapplication.ui.InicioFragment
import com.example.myapplication.ui.PerfilFragment

class InicioActivity : AppCompatActivity() {
    private lateinit var dlayMenu : DrawerLayout
    private lateinit var nvMenu : NavigationView
    private lateinit var ivMenu : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)

        dlayMenu = findViewById(R.id.dlayMenu)
        nvMenu = findViewById(R.id.nvMenu)
        ivMenu = findViewById(R.id.ivMenu)

        ivMenu.setOnClickListener {
            dlayMenu.open()
        }

        nvMenu.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true // Define que se seleccione el ítem
            dlayMenu.closeDrawers() // Cierra el menú desplegable

            // Maneja las selecciones
            when (menuItem.itemId) {
                R.id.itInicio -> replaceFragment(InicioFragment())
                R.id.itEstadisticas -> replaceFragment(EstadisticasFragment())
                R.id.itPerfil -> replaceFragment(PerfilFragment())
            }
            true
        }

        // Hace que el teclado del dispositivo no tape a los Views (EditText, TextInputEditText, etc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dlayMenu)) { v, insets ->
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

        replaceFragment(InicioFragment())
    }

    private fun replaceFragment(fragment : Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragments, fragment)
            .commit()
    }
}