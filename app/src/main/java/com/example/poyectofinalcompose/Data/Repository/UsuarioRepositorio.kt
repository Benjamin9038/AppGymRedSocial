package com.example.poyectofinalcompose.Data.Repository


import com.example.poyectofinalcompose.Data.Model.Usuario

class UserRepository {
    private var usuario: Usuario? = null

    fun guardarUsuario(nuevoUsuario: Usuario) {
        usuario = nuevoUsuario
    }

    fun obtenerUsuario(): Usuario? {
        return usuario
    }
}