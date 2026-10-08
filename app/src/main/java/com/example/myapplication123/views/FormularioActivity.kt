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

// Pantalla de formulario para crear y editar registros de temperatura y ventilador
class FormularioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_formulario)

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

        // Carga de opciones de ambiente en el Spinner
        configurarSpinner(spinnerAmbiente)

        // Acción al presionar el botón Guardar con validaciones correspondientes
        btnGuardar.setOnClickListener {
            val tempTexto = etTemperatura.text.toString().trim()
            val fechaHora = etFechaHora.text.toString().trim()

            if (validarCampos(etTemperatura, etFechaHora, tempTexto, fechaHora)) {
                Toast.makeText(this, "Registro guardado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
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
