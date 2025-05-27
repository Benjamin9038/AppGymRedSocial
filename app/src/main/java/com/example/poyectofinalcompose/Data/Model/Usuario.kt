package com.example.poyectofinalcompose.Data.Model
// Modelo de datos que representa un usuario registrado en la app
data class Usuario(
    val uid: String = "",
    val email: String = "",
    val nombre: String = "",
    val edad: Int = 0,
    val peso: Double = 0.0,
    val altura: Double = 0.0,
    val genero: String = "",
    val gymId: String = "",
    val actividadDeporFav: String = "",
    val tiempoEntrenando: String = "",
    val fotoUrl: String? = null // URL de la imagen en Firebase Storage

)
