package com.example.myapplication123.views

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication123.R

// Pantalla principal de inicio de sesión
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Ajusta los márgenes de la vista según las barras del sistema (Edge to Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        // Acción al presionar el botón de inicio de sesión
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validarCampos(etEmail, etPassword, email, password)) {
                // Navega a Bienvenida enviando el email por Intent
                val intent = Intent(this, BienvenidaActivity::class.java).apply {
                    putExtra("EXTRA_EMAIL", email)
                }
                startActivity(intent)
            }
        }

        // Navegación hacia la pantalla de registro
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    // Valida que el email tenga formato válido con @ y dominio, y que la contraseña cumpla el mínimo
    private fun validarCampos(
        etEmail: EditText,
        etPassword: EditText,
        email: String,
        password: String
    ): Boolean {
        var esValido = true

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

        return esValido
    }
}
