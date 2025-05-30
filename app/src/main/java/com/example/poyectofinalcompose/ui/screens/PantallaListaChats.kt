package com.example.poyectofinalcompose.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import com.example.poyectofinalcompose.Data.Model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.net.Uri
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.poyectofinalcompose.Data.Model.Mensaje
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaChats(
    localNavController: NavController, // NavController para moverse dentro del BottomBar
    globalNavController: NavController // NavController global para navegar a PantallaChat
)
 {
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance() // Referencia a la base de datos de Firestore

    var usuariosConChat by remember { mutableStateOf<List<Usuario>>(emptyList()) } // Usuarios con los que hay chat
    var cargando by remember { mutableStateOf(true) }
    var imagenAmpliada by remember { mutableStateOf<String?>(null) } // Estado para mostrar imagen ampliada

     val mensajesPorUsuario = remember { mutableStateMapOf<String, Mensaje>() } // Último mensaje por UID


     // Efecto que se ejecuta al abrir la pantalla
     LaunchedEffect(currentUid) {
         if (currentUid == null) return@LaunchedEffect

         // Obtener todos los chats
         db.collection("chats").get().addOnSuccessListener { chatDocs ->
             val otrosUids = mutableSetOf<String>()

             for (doc in chatDocs) {
                 val ids = doc.id.split("_")
                 if (ids.contains(currentUid) && ids.size == 2) {
                     val otro = if (ids[0] == currentUid) ids[1] else ids[0]
                     otrosUids.add(otro)

                     //Cargar el ultimo mensaje
                     db.collection("chats").document(doc.id)
                         .collection("mensajes")
                         .orderBy("timestamp", Query.Direction.DESCENDING)
                         .limit(1)
                         .get()
                         .addOnSuccessListener { mensajes ->
                             mensajes.firstOrNull()?.toObject(Mensaje::class.java)?.let {
                                 mensajesPorUsuario[otro] = it
                             }
                         }
                 }
             }

             //si no hay uid no hay chat
             if (otrosUids.isEmpty()) {
                 usuariosConChat = emptyList()
                 cargando = false
                 return@addOnSuccessListener
             }

             // Obtener los usuarios a partir de sus UIDs
             db.collection("users").whereIn("uid", otrosUids.toList()).get()
                 .addOnSuccessListener { result ->
                     usuariosConChat = result.mapNotNull { it.toObject(Usuario::class.java) }
                     cargando = false
                 }
         }
     }


     // Mostrar imagen ampliada si se ha seleccionado una
    if (imagenAmpliada != null) {
        AlertDialog(
            onDismissRequest = { imagenAmpliada = null },
            confirmButton = {},
            text = {
                Image(
                    painter = rememberAsyncImagePainter(imagenAmpliada),
                    contentDescription = "Imagen ampliada",
                    modifier = Modifier
                        .size(250.dp)
                        .clip(RoundedCornerShape(4.dp)) // Bordes suavemente redondeados
                        .padding(8.dp)
                )
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Chats",
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold

                        )
                    }
                }
            )
        }
    ) { padding ->
        if (cargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (usuariosConChat.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes chats activos aún.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                ) {
                    items(usuariosConChat) { usuario ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    val fotoCodificada = Uri.encode(usuario.fotoUrl ?: "null")
                                    globalNavController.navigate("chat/${usuario.uid}/${usuario.nombre}/$fotoCodificada")


                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                if (!usuario.fotoUrl.isNullOrEmpty()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(usuario.fotoUrl),
                                        contentDescription = "Foto",
                                        contentScale = ContentScale.Crop, // Ajusta visualmente sin dejar márgenes blancos
                                        modifier = Modifier
                                            .size(width = 50.dp, height = 50.dp)
                                            .clip(RoundedCornerShape(35.dp)) // Bordes suavemente redondeados
                                            .clickable { imagenAmpliada = usuario.fotoUrl }
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Sin foto",
                                        modifier = Modifier.size(48.dp)
                                            .size(width = 50.dp, height = 50.dp)
                                            .clip(RoundedCornerShape(35.dp)) // Bordes suavemente redondeados

                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Mostrar nombre del usuario y último mensaje
                                val ultimoMensaje = mensajesPorUsuario[usuario.uid]
                                val horaOFecha = ultimoMensaje?.timestamp?.let { HoraOFechaMensaje(it) } ?: ""


                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = usuario.nombre,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (horaOFecha.isNotBlank()) {
                                            Text(
                                                text = horaOFecha,
                                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    if (ultimoMensaje != null) {
                                        Text(
                                            text = ultimoMensaje.contenido,
                                            color = Color(0xFF777777),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                            fontWeight = FontWeight.Normal,
                                            lineHeight = 18.sp
                                        )
                                    } else {
                                        Text(
                                            text = "Toca para continuar el chat",
                                            color = Color(0xFFAAAAAA),
                                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                            fontWeight = FontWeight.Normal
                                        )
                                    }
                                }



                            }
                        }
                    }
                }
            }
        }
    }
}

// Función que convierte el timestamp del mensaje en hora o fecha
fun HoraOFechaMensaje(timestamp: Long): String {
    val ahora = Calendar.getInstance()
    val fechaMensaje = Calendar.getInstance().apply { timeInMillis = timestamp }

    return if (
        ahora.get(Calendar.YEAR) == fechaMensaje.get(Calendar.YEAR) &&
        ahora.get(Calendar.DAY_OF_YEAR) == fechaMensaje.get(Calendar.DAY_OF_YEAR)
    ) {
        //si el mensaje es de hoy muestra la hora
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
    } else {
        //si no muestra la fecha
        SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(timestamp))
    }
}

