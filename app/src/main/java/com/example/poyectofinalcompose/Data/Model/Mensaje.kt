package com.example.poyectofinalcompose.Data.Model

data class Mensaje(
    val emisorUid: String = "", //UID del usuario que envía el mensaje
    val receptorUid: String = "", //UID del usuario que recibe el mensaje
    val contenido: String = "", //Texto del mensaje
    val timestamp: Long = System.currentTimeMillis() //orden cronológico (cuando se envío)
)
