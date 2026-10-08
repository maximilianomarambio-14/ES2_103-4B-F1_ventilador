package com.example.myapplication123.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication123.R
import com.example.myapplication123.adapters.RegistroAdapter
import com.example.myapplication123.models.RegistroVentilador
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

// Pantalla principal que muestra el listado de registros y permite agregar nuevos
class ListaActivity : AppCompatActivity() {

    private lateinit var adapter: RegistroAdapter
    private val listaRegistros = mutableListOf<RegistroVentilador>()

    // Instancia de Firestore y registro del listener en tiempo real
    private lateinit var firestore: FirebaseFirestore
    private var snapshotListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista)

        // Inicialización de Firestore
        firestore = FirebaseFirestore.getInstance()

        // Ajuste de márgenes según barras del sistema (Edge to Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listaLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rvRegistros = findViewById<RecyclerView>(R.id.rvRegistros)
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregar)
        val fabVolver = findViewById<FloatingActionButton>(R.id.fabVolver)

        // Configuración del RecyclerView con su adaptador
        configurarRecyclerView(rvRegistros)

        // Escucha en tiempo real de la colección registros_ventilador
        escucharRegistrosEnTiempoReal()

        // Botón para volver a la pantalla anterior
        fabVolver.setOnClickListener {
            finish()
        }

        // Navegación hacia el formulario para agregar un nuevo registro
        fabAgregar.setOnClickListener {
            val intent = Intent(this, FormularioActivity::class.java)
            startActivity(intent)
        }
    }

    // Inicializa el LayoutManager y el adapter para mostrar la lista con acciones de edición y eliminación
    private fun configurarRecyclerView(rv: RecyclerView) {
        rv.layoutManager = LinearLayoutManager(this)
        adapter = RegistroAdapter(
            listaRegistros = listaRegistros,
            onItemClick = { registro ->
                // Abre el formulario con los datos cargados para edición
                abrirFormularioParaEdicion(registro)
            },
            onEliminarClick = { registro, _ ->
                // Muestra cuadro de diálogo de confirmación antes de eliminar de Firestore
                mostrarDialogoEliminar(registro)
            }
        )
        rv.adapter = adapter
    }

    // Navega a FormularioActivity enviando el id y todos los campos del registro
    private fun abrirFormularioParaEdicion(registro: RegistroVentilador) {
        val intent = Intent(this, FormularioActivity::class.java).apply {
            putExtra("EXTRA_ID", registro.id)
            putExtra("EXTRA_AMBIENTE", registro.ambiente)
            putExtra("EXTRA_TEMPERATURA", registro.temperatura)
            putExtra("EXTRA_VENTILADOR", registro.ventiladorEncendido)
            putExtra("EXTRA_FECHA_HORA", registro.fechaHora)
        }
        startActivity(intent)
    }

    // Cuadro de diálogo de confirmación para eliminar el documento en Firestore
    private fun mostrarDialogoEliminar(registro: RegistroVentilador) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar el registro de '${registro.ambiente}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarRegistroFirestore(registro.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Elimina el documento de Firestore; la lista se actualiza automáticamente con el snapshot listener
    private fun eliminarRegistroFirestore(id: String) {
        if (id.isEmpty()) {
            Toast.makeText(this, "Error: identificador no válido", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("registros_ventilador")
            .document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    // Escucha en tiempo real los cambios en la colección registros_ventilador con Firestore
    private fun escucharRegistrosEnTiempoReal() {
        snapshotListener = firestore.collection("registros_ventilador")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error al cargar registros: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val nuevosRegistros = snapshot.toObjects(RegistroVentilador::class.java)
                    adapter.actualizarLista(nuevosRegistros)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remueve el listener de Firestore para evitar fugas de memoria
        snapshotListener?.remove()
    }
}
