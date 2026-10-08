package com.example.myapplication123.models

import com.google.firebase.firestore.DocumentId

// Modelo de datos que representa una lectura y estado del ventilador en un ambiente
data class RegistroVentilador(
    @DocumentId
    val id: String = "",
    val ambiente: String = "",
    val temperatura: Double = 0.0,
    val ventiladorEncendido: Boolean = false,
    val fechaHora: String = ""
)
