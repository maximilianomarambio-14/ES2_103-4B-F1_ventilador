package com.example.myapplication123.views

import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Pantalla para el registro de nuevos usuarios
class RegistroActivity : AppCompatActivity() {

    // Instancias de Firebase Authentication y Firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        // Inicialización de Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // Botón para volver a la pantalla de Login
        btnVolver.setOnClickListener {
            finish()
        }

        // Acción al presionar el botón de registro
        btnRegister.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Valida los campos antes de cualquier llamada a Firebase
            if (validarRegistro(etNombre, etEmail, etPassword, etConfirmPassword, nombre, email, password, confirmPassword)) {
                registrarUsuarioEnFirebase(nombre, email, password)
            }
        }
    }

    // Registra la cuenta en FirebaseAuth y almacena el documento en Firestore
    private fun registrarUsuarioEnFirebase(nombre: String, email: String, password: String) {
        // Creación del usuario con email y contraseña
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""

                    // Datos del usuario para la colección 'usuarios'
                    val usuarioMap = hashMapOf(
                        "uid" to uid,
                        "nombre" to nombre,
                        "email" to email
                    )

                    // Guarda el documento en Firestore en la colección 'usuarios'
                    firestore.collection("usuarios")
                        .document(uid)
                        .set(usuarioMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

                            // Navega a Bienvenida enviando el email por Intent
                            val intent = Intent(this, BienvenidaActivity::class.java).apply {
                                putExtra("EXTRA_EMAIL", email)
                            }
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            // En caso de fallo al guardar en Firestore, muestra el mensaje de error
                            Toast.makeText(this, e.localizedMessage ?: "Error al guardar usuario en base de datos", Toast.LENGTH_LONG).show()
                        }
                } else {
                    // En caso de fallo en autenticación, muestra el mensaje de error de Firebase
                    val errorMessage = task.exception?.localizedMessage ?: "Error al registrar cuenta"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
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
