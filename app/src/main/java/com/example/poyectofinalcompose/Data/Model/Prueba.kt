package com.example.poyectofinalcompose.Data.Model
//Representa una prueba física dentro de la aplicación.
data class Prueba(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val descripcion: String,
    val imagen: Int,
    val enlace: String
)