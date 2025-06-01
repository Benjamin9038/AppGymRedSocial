package com.example.poyectofinalcompose.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.poyectofinalcompose.Data.Model.Usuario
import com.example.poyectofinalcompose.Data.Repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaUsuariosPorGimnasio(
    bottomNavController: NavController, // NavController interno
    globalNavController: NavController, // NavController global
    gymId: String // ID del gimnasio seleccionado
) {
    val userRepository = remember { UserRepository() }  // Repositorio para acceder a usuarios de Firestore

    // Estados del componente
    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) } // Lista de usuarios del gimnasio
    var mostrarCargando by remember { mutableStateOf(true) } // Estado de carga
    var error by remember { mutableStateOf<String?>(null) } // Mensaje de error (si no hay usuarios)
    var imagenAmpliada by remember { mutableStateOf<String?>(null) } // Para mostrar imagen ampliada

    // Al entrar en la pantalla, cargamos los usuarios del gimnasio
    LaunchedEffect(gymId) {
        userRepository.obtenerUsuariosPorGimnasio(gymId) { result ->
            val usuarioActual = FirebaseAuth.getInstance().currentUser?.uid
            usuarios = result.filter { it.uid != usuarioActual } // Excluir al usuario actual
            mostrarCargando = false
            if (usuarios.isEmpty()) error = "No hay usuarios registrados en este gimnasio."
        }
    }

    // Si hay imagen seleccionada se muestra en grande con un AlertDialog
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
                        .clip(RoundedCornerShape(16.dp))
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )
            }
        )
    }

    // Contenedor principal de la pantalla con barra superior
    Scaffold(
        containerColor = Color(0xFFF9F9F9),
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
        if (usuarios.isEmpty() && !mostrarCargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no existe ningún usuario en este gimnasio",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        } else {
            // Lista de usuarios
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(12.dp)
            ) {
                // Color de fondo según experiencia del usuario
                items(usuarios) { usuario ->
                    val colorFondo = when (usuario.tiempoEntrenando.lowercase()) {
                        "menos de 6 meses" -> Color(0xFFEEEEEE) // gris
                        "6-12 meses entrenados" -> Color(0xFFE3F2FD) // Azul claro
                        "1-3 años entrenados" -> Color(0xFFBBDEFB) // Azul medio
                        "más de 3 años" -> Color(0xFF90CAF9) // Azul oscuro
                        else -> Color.White
                    }

                    //Tarjeta del usuario
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                // Al pulsar, se navega al chat con ese usuario
                                val fotoCodificada = Uri.encode(usuario.fotoUrl ?: "null")
                                globalNavController.navigate("chat/${usuario.uid}/${usuario.nombre}/$fotoCodificada")
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorFondo),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!usuario.fotoUrl.isNullOrBlank()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(usuario.fotoUrl),
                                        contentDescription = "Foto de ${usuario.nombre}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { imagenAmpliada = usuario.fotoUrl }
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Sin foto",
                                        modifier = Modifier.size(90.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(usuario.nombre, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    AssistChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                "Edad: ${usuario.edad}",
                                                fontSize = MaterialTheme.typography.bodySmall.fontSize
                                            )
                                        },
                                        modifier = Modifier.height(26.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    AssistChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                usuario.actividadDeporFav,
                                                fontSize = MaterialTheme.typography.bodySmall.fontSize
                                            )
                                        },
                                        modifier = Modifier.height(26.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    AssistChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                usuario.tiempoEntrenando,
                                                fontSize = MaterialTheme.typography.bodySmall.fontSize
                                            )
                                        },
                                        modifier = Modifier.height(26.dp)
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