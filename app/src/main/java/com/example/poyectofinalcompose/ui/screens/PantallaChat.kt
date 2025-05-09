package com.example.poyectofinalcompose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.poyectofinalcompose.Data.Model.Mensaje
import com.example.poyectofinalcompose.Data.Repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaChat(navController: NavController, receptorUid: String, receptorNombre: String) {
    val chatRepository = remember { ChatRepository() }
    val usuarioActual = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var mensajeTexto by remember { mutableStateOf("") }
    var listaMensajes by remember { mutableStateOf<List<Mensaje>>(emptyList()) }

    // Escucha en tiempo real los mensajes entre los dos usuarios
    LaunchedEffect(Unit) {
        chatRepository.obtenerMensajes(usuarioActual, receptorUid) { mensajes ->
            listaMensajes = mensajes
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat con $receptorNombre") }
            )
        },
        //La barra inferior incluye la caja de texto y el boton de enviar
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Campo de texto para escribir el mensaje
                OutlinedTextField(
                    value = mensajeTexto,
                    onValueChange = { mensajeTexto = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje...") }
                )
                //Boton de enviar
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (mensajeTexto.isNotBlank()) {
                        val mensaje = Mensaje(
                            emisorUid = usuarioActual,
                            receptorUid = receptorUid,
                            contenido = mensajeTexto
                        )
                        chatRepository.enviarMensaje(mensaje) { success, error ->
                            if (success) mensajeTexto = ""

                        }
                    }
                }) {
                    Text("Enviar")
                }
            }
        }
    ) { padding ->
        //Comienza el contenido principal del Scaffold y
        // muestra la lista de mensajes en una columna desplazable
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp)
        ) {
            items(listaMensajes) { mensaje ->
                //Alinea los mensajes a la derecha si son del usuario actual,
                //a la izquierda si son del otro
                val mensajeMio = mensaje.emisorUid == usuarioActual
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (mensajeMio) Arrangement.End else Arrangement.Start
                ) {
                    //Cada mensaje se muestra como una burbuja con color diferente segun quien lo envia
                    Surface(
                        color = if (mensajeMio) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = mensaje.contenido,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}
