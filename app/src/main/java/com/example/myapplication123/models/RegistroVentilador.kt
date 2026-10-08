package com.example.myapplication123.models

// Modelo de datos que representa una lectura y estado del ventilador en un ambiente
// NOTA: El campo 'id' llevará @DocumentId cuando se integre con Cloud Firestore
data class RegistroVentilador(
    val id: String = "",
    val ambiente: String = "",
    val temperatura: Double = 0.0,
    val ventiladorEncendido: Boolean = false,
    val fechaHora: String = ""
)
