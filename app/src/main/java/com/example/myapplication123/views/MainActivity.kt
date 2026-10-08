package com.example.myapplication123.views

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication123.R
import com.google.firebase.auth.FirebaseAuth

// Pantalla principal de inicio de sesión
class MainActivity : AppCompatActivity() {

    // Instancia de Firebase Authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Inicialización de FirebaseAuth
        auth = FirebaseAuth.getInstance()

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

            // Valida los campos antes de cualquier intento con Firebase
            if (validarCampos(etEmail, etPassword, email, password)) {
                iniciarSesionConFirebase(email, password)
            }
        }

        // Navegación hacia la pantalla de registro
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    // Inicia sesión en FirebaseAuth con email y contraseña
    private fun iniciarSesionConFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                    // Navega a Bienvenida enviando el email por Intent
                    val intent = Intent(this, BienvenidaActivity::class.java).apply {
                        putExtra("EXTRA_EMAIL", email)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    // En caso de error (credenciales incorrectas u otros), muestra el mensaje y permanece en Login
                    val errorMessage = task.exception?.localizedMessage ?: "Error de autenticación"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
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
