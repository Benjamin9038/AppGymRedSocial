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
    bottomNavController: NavController,
    globalNavController: NavController,
    gymId: String
) {
    val userRepository = remember { UserRepository() }

    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var mostrarCargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var imagenAmpliada by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(gymId) {
        userRepository.obtenerUsuariosPorGimnasio(gymId) { result ->
            val usuarioActual = FirebaseAuth.getInstance().currentUser?.uid
            usuarios = result.filter { it.uid != usuarioActual }
            mostrarCargando = false
            if (usuarios.isEmpty()) error = "No hay usuarios registrados en este gimnasio."
        }
    }

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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(12.dp)
            ) {
                items(usuarios) { usuario ->
                    val colorFondo = when (usuario.tiempoEntrenando.lowercase()) {
                        "menos de 6 meses" -> Color(0xFFE0E0E0) // gris
                        "6-12 meses entrenados" -> Color(0xFFE8F5E9) // verde claro
                        "1-3 años entrenados" -> Color(0xFFE3F2FD) // azul claro
                        "más de 3 años" -> Color(0xFFD1C4E9) // morado claro
                        else -> Color.White
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
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