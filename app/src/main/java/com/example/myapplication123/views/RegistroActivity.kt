package com.example.myapplication123.views

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication123.R

// Pantalla para el registro de nuevos usuarios
class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        // Ajusta los márgenes según las barras del sistema (Edge to Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registroLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        // Acción al presionar el botón de registro
        btnRegister.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (validarRegistro(etNombre, etEmail, etPassword, etConfirmPassword, nombre, email, password, confirmPassword)) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Valida nombre no vacío, formato de correo con @ y dominio, longitud mínima y coincidencia de contraseña
    private fun validarRegistro(
        etNombre: EditText,
        etEmail: EditText,
        etPassword: EditText,
        etConfirmPassword: EditText,
        nombre: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var esValido = true

        if (nombre.isEmpty()) {
            etNombre.error = "El nombre no puede estar vacío"
            esValido = false
        }

        if (email.isEmpty()) {
            etEmail.error = "Ingresa tu correo"
            esValido = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Correo electrónico inválido (debe contener @ y dominio)"
            esValido = false
        }

        if (password.length < 6) {
            etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            esValido = false
        }

        if (confirmPassword != password) {
            etConfirmPassword.error = "Las contraseñas no coinciden"
            esValido = false
        }

        return esValido
    }
}
