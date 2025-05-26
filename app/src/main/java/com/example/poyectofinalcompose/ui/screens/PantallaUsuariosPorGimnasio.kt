package com.example.poyectofinalcompose.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.poyectofinalcompose.Data.Model.Usuario
import com.example.poyectofinalcompose.Data.Repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import android.net.Uri
import androidx.compose.ui.text.style.TextAlign


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaUsuariosPorGimnasio(
    bottomNavController: NavController,
    globalNavController: NavController,
    gymId: String
) {
    val userRepository = remember { UserRepository() }
    val azulMarino = Color(0xFF005A9C)

    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var mostrarCargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var imagenAmpliada by remember { mutableStateOf<String?>(null) } // Estado para mostrar imagen ampliada

    // Se ejecuta una vez cuando cambia el gymId
    LaunchedEffect(gymId) {
        println("Buscando usuarios con gymId: '$gymId'")

        // Se obtiene la lista de usuarios desde Firestore
        userRepository.obtenerUsuariosPorGimnasio(gymId) { result ->
            val usuarioActual = FirebaseAuth.getInstance().currentUser?.uid // id del usuario que ha iniciado sesion

            // Se filtra la lista para que no se incluya el propio usuario logueado
            usuarios = result.filter { it.uid != usuarioActual }

            mostrarCargando = false

            if (usuarios.isEmpty()) {
                error = "No hay usuarios registrados en este gimnasio."
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
                            text = "Usuarios en $gymId",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }
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
                                    .border(1.dp, azulMarino, shape = MaterialTheme.shapes.medium)
                                    .clickable {
                                        val fotoCodificada = Uri.encode(usuario.fotoUrl ?: "null")
                                        globalNavController.navigate("chat/${usuario.uid}/${usuario.nombre}/$fotoCodificada")
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!usuario.fotoUrl.isNullOrBlank()) {
                                        Image(
                                            painter = rememberAsyncImagePainter(usuario.fotoUrl),
                                            contentDescription = "Foto de ${usuario.nombre}",
                                            contentScale = ContentScale.Crop, // Ajusta visualmente sin dejar márgenes blancos
                                            modifier = Modifier
                                                .padding(start = 2.dp)
                                                .size(width = 70.dp, height = 100.dp)
                                                .clip(RoundedCornerShape(16.dp)) // Bordes suavemente redondeados
                                                .clickable { imagenAmpliada = usuario.fotoUrl }
                                        )

                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Sin foto",
                                            modifier = Modifier
                                                .padding(start = 2.dp)
                                                .size(width = 70.dp, height = 100.dp)

                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column {
                                        //De esta manera puedo poner solo en negrita nombre, edad, grupo favorito... sin
                                        //necesidad de que lo sea también su contenido
                                        Text(buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Nombre: ") }
                                            append(usuario.nombre)
                                        })
                                        Text(buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Edad: ") }
                                            append(usuario.edad.toString())
                                        })
                                        Text(buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Grupo favorito: ") }
                                            append(usuario.grupoMuscularFavorito)
                                        })
                                        Text(buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Tiempo entrenando: ") }
                                            append(usuario.tiempoEntrenando)
                                        })
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

