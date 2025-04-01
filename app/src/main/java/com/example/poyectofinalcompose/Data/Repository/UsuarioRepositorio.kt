package com.example.poyectofinalcompose.Data.Repository


import com.example.poyectofinalcompose.Data.Model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
// Repositorio encargado de gestionar los datos del usuario en Firestore
class UserRepository {
    private val db = FirebaseFirestore.getInstance()

    // Guarda o actualiza un usuario en la colección usuarios
    fun guardarUsuario(usuario: Usuario, onResult: (Boolean, String?) -> Unit) {
        db.collection("users").document(usuario.uid).set(usuario)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}
