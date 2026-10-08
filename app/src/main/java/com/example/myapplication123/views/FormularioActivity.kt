package com.example.myapplication123.views

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication123.R
import com.example.myapplication123.models.RegistroVentilador
import com.google.firebase.firestore.FirebaseFirestore

// Pantalla de formulario para crear y editar registros de temperatura y ventilador
class FormularioActivity : AppCompatActivity() {

    // Instancia de Firestore
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_formulario)

        // Inicialización de Firestore
        firestore = FirebaseFirestore.getInstance()

        // Ajuste de márgenes según las barras del sistema (Edge to Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.formularioLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spinnerAmbiente = findViewById<Spinner>(R.id.spinnerAmbiente)
        val etTemperatura = findViewById<EditText>(R.id.etTemperatura)
        val switchVentilador = findViewById<SwitchCompat>(R.id.switchVentilador)
        val etFechaHora = findViewById<EditText>(R.id.etFechaHora)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        // Botón Cancelar: cierra sin guardar ni realizar llamadas a Firebase
        btnCancelar.setOnClickListener {
            finish()
        }

        // Carga de opciones de ambiente en el Spinner
        configurarSpinner(spinnerAmbiente)

        // Verificación de si la actividad se abrió para editar un registro existente
        val registroId = intent.getStringExtra("EXTRA_ID")
        val esEdicion = !registroId.isNullOrEmpty()

        if (esEdicion) {
            val ambienteActual = intent.getStringExtra("EXTRA_AMBIENTE") ?: ""
            val tempActual = intent.getDoubleExtra("EXTRA_TEMPERATURA", 0.0)
            val ventiladorActual = intent.getBooleanExtra("EXTRA_VENTILADOR", false)
            val fechaHoraActual = intent.getStringExtra("EXTRA_FECHA_HORA") ?: ""

            // Carga de datos en las vistas del formulario
            etTemperatura.setText(tempActual.toString())
            switchVentilador.isChecked = ventiladorActual
            etFechaHora.setText(fechaHoraActual)

            // Selección del ambiente en el Spinner
            @Suppress("UNCHECKED_CAST")
            val spinnerAdapter = spinnerAmbiente.adapter as? ArrayAdapter<String>
            val posicionAmbiente = spinnerAdapter?.getPosition(ambienteActual) ?: -1
            if (posicionAmbiente >= 0) {
                spinnerAmbiente.setSelection(posicionAmbiente)
            }
        }

        // Acción al presionar el botón Guardar con validaciones correspondientes
        btnGuardar.setOnClickListener {
            val tempTexto = etTemperatura.text.toString().trim()
            val fechaHora = etFechaHora.text.toString().trim()

            // Valida los campos y evita llamar a Firebase si la validación falla
            if (validarCampos(etTemperatura, etFechaHora, tempTexto, fechaHora)) {
                val ambiente = spinnerAmbiente.selectedItem?.toString() ?: ""
                val temperatura = tempTexto.toDouble()
                val ventiladorEncendido = switchVentilador.isChecked

                if (esEdicion && registroId != null) {
                    // Actualiza el documento existente en Firestore
                    actualizarRegistroEnFirestore(registroId, ambiente, temperatura, ventiladorEncendido, fechaHora)
                } else {
                    // Crea un nuevo registro en Firestore
                    val nuevoRegistro = RegistroVentilador(
                        ambiente = ambiente,
                        temperatura = temperatura,
                        ventiladorEncendido = ventiladorEncendido,
                        fechaHora = fechaHora
                    )
                    guardarRegistroEnFirestore(nuevoRegistro)
                }
            }
        }
    }

    // Actualiza el registro existente en Firestore con update()
    private fun actualizarRegistroEnFirestore(
        id: String,
        ambiente: String,
        temperatura: Double,
        ventiladorEncendido: Boolean,
        fechaHora: String
    ) {
        val datosActualizados = mapOf<String, Any>(
            "ambiente" to ambiente,
            "temperatura" to temperatura,
            "ventiladorEncendido" to ventiladorEncendido,
            "fechaHora" to fechaHora
        )

        firestore.collection("registros_ventilador")
            .document(id)
            .update(datosActualizados)
            .addOnSuccessListener {
                Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.localizedMessage ?: "Error al actualizar registro", Toast.LENGTH_LONG).show()
            }
    }

    // Guarda el registro en la colección registros_ventilador usando add()
    private fun guardarRegistroEnFirestore(registro: RegistroVentilador) {
        firestore.collection("registros_ventilador")
            .add(registro)
            .addOnSuccessListener {
                Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.localizedMessage ?: "Error al guardar el registro", Toast.LENGTH_LONG).show()
            }
    }

    // Configura el Spinner con la lista de ambientes predeterminados
    private fun configurarSpinner(spinner: Spinner) {
        val ambientes = listOf("Dormitorio", "Sala", "Oficina", "Bodega")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ambientes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    // Valida que los campos requeridos no estén vacíos y que la temperatura sea un número válido
    private fun validarCampos(
        etTemperatura: EditText,
        etFechaHora: EditText,
        tempTexto: String,
        fechaHora: String
    ): Boolean {
        var esValido = true

        if (tempTexto.isEmpty()) {
            etTemperatura.error = "Ingresa la temperatura"
            esValido = false
        } else {
            val tempNumero = tempTexto.toDoubleOrNull()
            if (tempNumero == null) {
                etTemperatura.error = "Temperatura numérica inválida"
                esValido = false
            }
        }

        if (fechaHora.isEmpty()) {
            etFechaHora.error = "Ingresa la fecha y hora"
            esValido = false
        }

        return esValido
    }
}
