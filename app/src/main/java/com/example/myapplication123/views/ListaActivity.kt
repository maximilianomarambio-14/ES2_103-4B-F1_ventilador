package com.example.myapplication123.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication123.R
import com.example.myapplication123.adapters.RegistroAdapter
import com.example.myapplication123.models.RegistroVentilador
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Pantalla principal que muestra el listado de registros y permite agregar nuevos
class ListaActivity : AppCompatActivity() {

    private lateinit var adapter: RegistroAdapter

    // TEMPORAL: Lista de datos de ejemplo local mientras no se use Firestore
    private val listaRegistros = mutableListOf(
        RegistroVentilador(
            id = "1",
            ambiente = "Sala",
            temperatura = 25.4,
            ventiladorEncendido = true,
            fechaHora = "2026-10-07 15:30"
        ),
        RegistroVentilador(
            id = "2",
            ambiente = "Dormitorio",
            temperatura = 21.0,
            ventiladorEncendido = false,
            fechaHora = "2026-10-07 18:00"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista)

        // Ajuste de márgenes según barras del sistema (Edge to Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listaLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rvRegistros = findViewById<RecyclerView>(R.id.rvRegistros)
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregar)

        // Configuración del RecyclerView con su adaptador
        configurarRecyclerView(rvRegistros)

        // Navegación hacia el formulario para agregar un nuevo registro
        fabAgregar.setOnClickListener {
            val intent = Intent(this, FormularioActivity::class.java)
            startActivity(intent)
        }
    }

    // Inicializa el LayoutManager y el adapter con callback de eliminación
    private fun configurarRecyclerView(rv: RecyclerView) {
        rv.layoutManager = LinearLayoutManager(this)
        adapter = RegistroAdapter(listaRegistros) { registro, posicion ->
            listaRegistros.removeAt(posicion)
            adapter.notifyItemRemoved(posicion)
            Toast.makeText(this, "Eliminado: ${registro.ambiente}", Toast.LENGTH_SHORT).show()
        }
        rv.adapter = adapter
    }
}
