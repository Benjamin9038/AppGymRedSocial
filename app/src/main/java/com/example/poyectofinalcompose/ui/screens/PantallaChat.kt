package com.example.poyectofinalcompose.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.poyectofinalcompose.Data.Model.Mensaje
import com.example.poyectofinalcompose.Data.Repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.lazy.rememberLazyListState
import java.text.SimpleDateFormat
import java.util.*




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaChat(navController: NavController, receptorUid: String, receptorNombre: String, receptorFotoUrl: String?)
 {
     // Repositorio para interactuar con Firestore (mensajes)
    val chatRepository = remember { ChatRepository() }
    val usuarioActual = FirebaseAuth.getInstance().currentUser?.uid ?: ""

     // Texto actual del mensaje que está escribiendo el usuario
     var mensajeTexto by remember { mutableStateOf("") }
    var listaMensajes by remember { mutableStateOf<List<Mensaje>>(emptyList()) }

     //para la posicion de los mensajes mientras escribes
     val listState = rememberLazyListState()
     // Estado del scroll de la lista de mensajes
     val timestamp: Long = System.currentTimeMillis()

     val azulMarino = Color(0xFF005A9C)
     var imagenAmpliada by remember { mutableStateOf(false) }

    // Escucha en tiempo real los mensajes entre los dos usuarios
    LaunchedEffect(Unit) {
        chatRepository.obtenerMensajes(usuarioActual, receptorUid) { mensajes ->
            listaMensajes = mensajes
        }
    }

     // Cuando se actualiza la lista de mensajes, baja automáticamente al último mensaje
     LaunchedEffect(listaMensajes) {
         if (listaMensajes.isNotEmpty()) {
             listState.animateScrollToItem(listaMensajes.lastIndex)
         }
     }

     // Estructura principal con TopBar, LazyColumn y barra inferior input
     Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!receptorFotoUrl.isNullOrEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(receptorFotoUrl),
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .clickable { imagenAmpliada = true },
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Sin foto",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = receptorNombre,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black
                    )
                )
                Divider(color = Color.LightGray, thickness = 0.8.dp)
            }


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
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp)),
                    placeholder = { Text("Escribe un mensaje...") },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = azulMarino,
                        unfocusedBorderColor = azulMarino,
                        cursorColor = azulMarino
                    )
                )


                //Boton de enviar
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (mensajeTexto.isNotBlank()) {
                            val mensaje = Mensaje(
                                emisorUid = usuarioActual,
                                receptorUid = receptorUid,
                                contenido = mensajeTexto
                            )
                            chatRepository.enviarMensaje(mensaje) { success, _ ->
                                if (success) mensajeTexto = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = azulMarino),
                    shape = CircleShape,
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Text("→", color = Color.White)
                }

            }
        }
    ) { padding ->
        //Comienza el contenido principal del Scaffold y
        // muestra la lista de mensajes en una columna desplazable
         LazyColumn(
             state = listState,
             modifier = Modifier
                 .fillMaxSize()
                 .padding(padding)
                 .padding(8.dp)
                 .imePadding() // para que se levante con el teclado
         ) {
             items(listaMensajes) { mensaje ->

                 val hora = remember(mensaje.timestamp) {
                     val date = Date(mensaje.timestamp)
                     SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
                 }
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
                        color = if (mensajeMio) azulMarino else MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .padding(4.dp)
                            .widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = mensaje.contenido,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = hora,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }

                }
            }
        }
    }

     if (imagenAmpliada && !receptorFotoUrl.isNullOrEmpty()) {
         AlertDialog(
             onDismissRequest = { imagenAmpliada = false },
             confirmButton = {},
             text = {
                 Image(
                     painter = rememberAsyncImagePainter(receptorFotoUrl),
                     contentDescription = "Imagen ampliada",
                     modifier = Modifier
                         .size(250.dp)
                         .clip(RoundedCornerShape(8.dp))
                         .padding(8.dp),
                     contentScale = ContentScale.Crop
                 )
             }
         )
     }

 }
