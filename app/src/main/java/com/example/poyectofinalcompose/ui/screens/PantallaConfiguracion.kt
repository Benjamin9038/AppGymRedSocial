package com.example.poyectofinalcompose.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.poyectofinalcompose.Data.Model.Usuario
import com.example.poyectofinalcompose.Data.Repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConfiguracion(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val userRepository = remember { UserRepository() }
    val uid = auth.currentUser?.uid ?: return
    val context = LocalContext.current
    val azulMarino = Color(0xFF005A9C)

    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(true) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imagenUri = uri
    }

    LaunchedEffect(uid) {
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                usuario = doc.toObject(Usuario::class.java)
                cargando = false
            }
            .addOnFailureListener {
                errorMessage = "No se pudo cargar el perfil"
                cargando = false
            }
    }

    if (cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    usuario?.let { datos ->

        var nombre by remember { mutableStateOf(datos.nombre) }
        var edad by remember { mutableStateOf(datos.edad.toString()) }
        var gym by remember { mutableStateOf(datos.gymId) }
        var grupo by remember { mutableStateOf(datos.actividadDeporFav) }
        var tiempo by remember { mutableStateOf(datos.tiempoEntrenando) }

        val listaGimnasios = listOf("Basic Fit Albacete", "McFit Albacete", "Fitness Villarrobledo",
            "Tiger Villarrobledo", "FraileGym Villarrobledo", "Centro Albacete", "Otro")

        val listaGrupos = listOf("Crossfit", "Hipertrofia", "PowerLifting", "Cardio", "Arterofilia")
        val tiempos = listOf("menos de 6 meses", "6-12 meses entrenados", "1-3 años entrenados", "más de 3 años")

        var gimnasioExpanded by remember { mutableStateOf(false) }
        var grupoExpanded by remember { mutableStateOf(false) }
        var tiempoExpanded by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Editar Perfil", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulMarino,
                    unfocusedBorderColor = azulMarino,
                    focusedLabelColor = azulMarino,
                    cursorColor = azulMarino
                )
            )

            OutlinedTextField(
                value = edad,
                onValueChange = { edad = it },
                label = { Text("Edad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulMarino,
                    unfocusedBorderColor = azulMarino,
                    focusedLabelColor = azulMarino,
                    cursorColor = azulMarino
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Acerca de tus GymSkills", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(expanded = gimnasioExpanded, onExpandedChange = {
                gimnasioExpanded = !gimnasioExpanded
            }) {
                TextField(
                    value = gym,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Selecciona el Gimnasio en el que entrenas")
                        }
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = azulMarino,
                        unfocusedIndicatorColor = azulMarino,
                        focusedLabelColor = azulMarino,
                        cursorColor = azulMarino
                    )
                )
                DropdownMenu(expanded = gimnasioExpanded, onDismissRequest = { gimnasioExpanded = false }) {
                    listaGimnasios.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            gym = it
                            gimnasioExpanded = false
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(expanded = grupoExpanded, onExpandedChange = {
                grupoExpanded = !grupoExpanded
            }) {
                TextField(
                    value = grupo,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Actividad deportiva favorita")
                        }
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = azulMarino,
                        unfocusedIndicatorColor = azulMarino,
                        focusedLabelColor = azulMarino,
                        cursorColor = azulMarino
                    )
                )
                DropdownMenu(expanded = grupoExpanded, onDismissRequest = { grupoExpanded = false }) {
                    listaGrupos.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            grupo = it
                            grupoExpanded = false
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(expanded = tiempoExpanded, onExpandedChange = {
                tiempoExpanded = !tiempoExpanded
            }) {
                TextField(
                    value = tiempo,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Tiempo entrenado")
                        }
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = azulMarino,
                        unfocusedIndicatorColor = azulMarino,
                        focusedLabelColor = azulMarino,
                        cursorColor = azulMarino
                    )
                )
                DropdownMenu(expanded = tiempoExpanded, onDismissRequest = { tiempoExpanded = false }) {
                    tiempos.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            tiempo = it
                            tiempoExpanded = false
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Foto de perfil (opcional)", fontWeight = FontWeight.Bold)

            if (imagenUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imagenUri),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp).clip(MaterialTheme.shapes.medium)
                )
            } else if (!datos.fotoUrl.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(datos.fotoUrl),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp).clip(MaterialTheme.shapes.medium)
                )
            } else {
                Icon(Icons.Default.Person, contentDescription = "Sin imagen", modifier = Modifier.size(100.dp))
            }

            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = azulMarino)
            ) {
                Text("Seleccionar nueva imagen")
            }


            Spacer(modifier = Modifier.height(16.dp))

            val contexto = LocalContext.current

            Button(
                onClick = {
                    val usuarioActualizado = Usuario(
                        uid = uid,
                        email = datos.email,
                        nombre = nombre,
                        edad = edad.toIntOrNull() ?: 0,
                        peso = 0.0,
                        altura = 0.0,
                        genero = "",
                        gymId = gym,
                        actividadDeporFav = grupo,
                        tiempoEntrenando = tiempo,
                        fotoUrl = datos.fotoUrl
                    )

                    if (imagenUri != null) {
                        val ref = FirebaseStorage.getInstance().reference.child("fotos_perfil/$uid.jpg")
                        ref.putFile(imagenUri!!)
                            .continueWithTask { task ->
                                if (!task.isSuccessful) throw task.exception ?: Exception("Error al subir")
                                ref.downloadUrl
                            }
                            .addOnSuccessListener { uri ->
                                userRepository.guardarUsuario(usuarioActualizado.copy(fotoUrl = uri.toString())) { success, _ ->
                                    if (success) {
                                        Toast.makeText(contexto, "Cambios guardados con éxito", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    } else {
                        userRepository.guardarUsuario(usuarioActualizado) { success, _ ->
                            if (success) {
                                Toast.makeText(contexto, "Cambios guardados con éxito", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = azulMarino)
            ) {
                Text("Guardar cambios")
            }



            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
