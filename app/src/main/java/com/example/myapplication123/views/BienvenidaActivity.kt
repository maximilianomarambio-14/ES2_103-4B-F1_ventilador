package com.example.myapplication123.views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication123.R
import com.google.firebase.auth.FirebaseAuth

// Pantalla de bienvenida que muestra el saludo al usuario y opciones de navegación
class BienvenidaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bienvenida)

        // Ajuste de márgenes según las barras del sistema (Edge to Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bienvenidaLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvSaludo = findViewById<TextView>(R.id.tvSaludo)
        val btnPreferencias = findViewById<Button>(R.id.btnPreferencias)
        val btnLista = findViewById<Button>(R.id.btnLista)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)

        // Recepción y visualización del correo electrónico enviado desde el Login
        val email = intent.getStringExtra("EXTRA_EMAIL") ?: ""
        tvSaludo.text = "Hola, $email"

        // Navegación a la pantalla de configuración de preferencias
        btnPreferencias.setOnClickListener {
            val intent = Intent(this, PreferenciasActivity::class.java)
            startActivity(intent)
        }

        // Navegación a la lista de registros de ventiladores
        btnLista.setOnClickListener {
            val intent = Intent(this, ListaActivity::class.java)
            startActivity(intent)
        }

        // Cierre de sesión en Firebase y regreso al Login limpiando el historial de pantallas
        btnCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
