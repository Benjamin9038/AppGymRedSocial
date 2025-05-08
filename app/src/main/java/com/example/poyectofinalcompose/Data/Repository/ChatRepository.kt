package com.example.poyectofinalcompose.Data.Repository

import com.example.poyectofinalcompose.Data.Model.Mensaje
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()

    // Genera un ID único común entre dos usuarios
    private fun generarChatId(uid1: String, uid2: String): String {
        return listOf(uid1, uid2).sorted().joinToString("_") //Ordena la lista alfabéticamente y
                                                             //Une los elementos de la lista
                                                             //en un solo String, separados por el símbolo _
    }

    // Envía un mensaje entre dos usuarios
    fun enviarMensaje(mensaje: Mensaje, onResult: (Boolean, String?) -> Unit) {
        val chatId = generarChatId(mensaje.emisorUid, mensaje.receptorUid)

        db.collection("chats")
            .document(chatId)
            .collection("mensajes")
            .add(mensaje)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    // Escucha los mensajes en tiempo real entre dos usuarios
    fun obtenerMensajes(uid1: String, uid2: String, onMensajesRecibidos: (List<Mensaje>) -> Unit) {
        val chatId = generarChatId(uid1, uid2)

        db.collection("chats")
            .document(chatId)
            .collection("mensajes")
            .orderBy("timestamp", Query.Direction.ASCENDING) //Significa orden ascendente es decir, del más antiguo al más reciente
            .addSnapshotListener { snapshots, error -> //snapshot es una especie de "foto" del estado actual de los documentos que ha devuelto la consulta de Firestore.
                if (error != null || snapshots == null) {
                    onMensajesRecibidos(emptyList())
                    return@addSnapshotListener //Es un "listener en tiempo real" de Firebase Firestore.
                }

                val mensajes = snapshots.documents.mapNotNull { it.toObject(Mensaje::class.java) }
                onMensajesRecibidos(mensajes)
            }
    }
}
