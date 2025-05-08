package com.example.poyectofinalcompose.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.poyectofinalcompose.Data.Model.Usuario
import com.example.poyectofinalcompose.Data.Repository.UserRepository
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaUsuariosPorGimnasio(navController: NavController, gymId: String) {
    val userRepository = remember { UserRepository() }

    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var mostrarCargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Se ejecuta una vez cuando cambia el gymId
    LaunchedEffect(gymId) {
        println("Buscando usuarios con gymId: '$gymId'")

        // Se obtiene la lista de usuarios desde Firestore
        userRepository.obtenerUsuariosPorGimnasio(gymId) { result ->
            val usuarioActual = FirebaseAuth.getInstance().currentUser?.uid // id del usuario que ha iniciado sesion

            // Se filtra la lista para que no se incluya el propio usuario logueado
            if (usuarioActual != null) {
                val usuariosFiltrados = mutableListOf<Usuario>()

                for (usuario in result) {
                    if (usuario.uid != usuarioActual) {
                        usuariosFiltrados.add(usuario)
                    }
                }

                usuarios = usuariosFiltrados
            } else {
                usuarios = result // Si no se detecta usuario actual se muestra la lista completa
            }

            mostrarCargando = false

            if (usuarios.isEmpty()) {
                error = "No hay usuarios registrados en este gimnasio."
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Usuarios en $gymId",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Se muestra según el estado actual
            when {
                // Si no hay usuarios se muestra el mensaje
                error != null -> {
                    Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
                }

                // Si hay usuarios se muestra la lista
                else -> {
                    LazyColumn {
                        items(usuarios) { usuario ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        navController.navigate("chat/${usuario.uid}/${usuario.nombre}")
                                    }
                            )
                            {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Nombre: ${usuario.nombre}", fontWeight = FontWeight.Bold)
                                    Text("Grupo favorito: ${usuario.grupoMuscularFavorito}")
                                    Text("Tiempo entrenando: ${usuario.tiempoEntrenando}")
                                }
                            }
                        }
                    }
                }
            }
        }
        }
}


