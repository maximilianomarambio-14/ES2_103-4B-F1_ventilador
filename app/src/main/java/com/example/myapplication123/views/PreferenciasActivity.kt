package com.example.myapplication123.views

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication123.R

// Pantalla para configurar preferencias del usuario (modo automático y selección de ambiente)
class PreferenciasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_preferencias)

        // Ajuste de márgenes según las barras del sistema (Edge to Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.preferenciasLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spinnerAmbientes = findViewById<Spinner>(R.id.spinnerAmbientes)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // Botón para volver a la pantalla anterior
        btnVolver.setOnClickListener {
            finish()
        }

        // Inicialización y carga de opciones para el selector de ambientes
        configurarSpinnerAmbientes(spinnerAmbientes)
    }

    // Configura el adaptador del Spinner con las opciones de ambientes requeridas
    private fun configurarSpinnerAmbientes(spinner: Spinner) {
        val ambientes = listOf("Dormitorio", "Sala", "Oficina", "Bodega")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ambientes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}
