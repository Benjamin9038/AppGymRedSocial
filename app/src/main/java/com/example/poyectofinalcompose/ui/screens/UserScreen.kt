package com.example.poyectofinalcompose.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.poyectofinalcompose.Data.Model.Usuario
import com.example.poyectofinalcompose.Data.Repository.UserRepository
import com.example.poyectofinalcompose.Navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance() //Autenticación de Firebase
    val userRepository = remember { UserRepository() } //Repositorio para guardar usuarios
    val azulMarino = Color(0xFF005A9C)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }

    val listaGimnasios = listOf(
        "Basic Fit Albacete", "McFit Albacete", "Centro Albacete", "Altafit Albacete",
        "Fitness Villarrobledo", "Tiger Villarrobledo", "FraileGym Villarrobledo"
    )
    var gimnasioSeleccionado by remember { mutableStateOf(listaGimnasios[0]) }
    var gimnasioExpanded by remember { mutableStateOf(false) }

    val listaGrupos = listOf("Crossfit", "Hipertrofia", "PowerLifting", "Cardio", "Arterofilia")
    var actividadFav by remember { mutableStateOf(listaGrupos[0]) }
    var grupoExpanded by remember { mutableStateOf(false) }

    val tiempos = listOf("Menos de 6 meses", "6-12 meses entrenados", "1-3 años entrenados", "Más de 3 años")
    var tiempoEntrenando by remember { mutableStateOf(tiempos[0]) }
    var tiempoExpanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> imagenUri = uri }
    var passwordVisible by remember { mutableStateOf(false) }

    fun guardarUsuarioConFoto(uid: String, fotoUrl: String?) {
        val nuevoUsuario = Usuario(
            uid = uid,
            email = email,
            nombre = nombre,
            edad = edad.toIntOrNull() ?: 0,
            peso = 0.0,
            altura = 0.0,
            gymId = gimnasioSeleccionado,
            actividadDeporFav = actividadFav,
            tiempoEntrenando = tiempoEntrenando,
            fotoUrl = fotoUrl
        )
        userRepository.guardarUsuario(nuevoUsuario) { success, error ->
            if (success) {
                auth.signInWithEmailAndPassword(email.trim(), password.trim())
                    .addOnSuccessListener {
                        navController.navigate(Screen.BottomBar.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                    .addOnFailureListener {
                        errorMessage = "Cuenta creada pero fallo al iniciar sesión: ${it.message}"
                    }
            } else {
                errorMessage = "Error al guardar datos: $error"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear Cuenta en FitMate", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            shape = RoundedCornerShape(24.dp),
            label = {
                    Text("Correo electrónico")
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azulMarino,
                unfocusedBorderColor = azulMarino,
                focusedLabelColor = azulMarino,
                cursorColor = azulMarino
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            shape = RoundedCornerShape(24.dp),
            label = {
                Text(
                    text = "Contraseña",
                )
            }
            ,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = description)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azulMarino,
                unfocusedBorderColor = azulMarino,
                focusedLabelColor = azulMarino,
                cursorColor = azulMarino
            )
        )


        OutlinedTextField(
            value = nombre,
            shape = RoundedCornerShape(24.dp),
            onValueChange = { nombre = it },
            label = {
                    Text("Nombre")

            },
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
            shape = RoundedCornerShape(24.dp),
            onValueChange = { edad = it },
            label = {
                    Text("Edad")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azulMarino,
                unfocusedBorderColor = azulMarino,
                focusedLabelColor = azulMarino,
                cursorColor = azulMarino
            )
        )

        // Dropdown de gimnasio
        Spacer(modifier = Modifier.height(16.dp))
        Text("Acerca de tus datos en el Gym", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))
        ExposedDropdownMenuBox(expanded = gimnasioExpanded, onExpandedChange = { gimnasioExpanded = !gimnasioExpanded }) {
            TextField(
                value = gimnasioSeleccionado,
                shape = RoundedCornerShape(24.dp),
                onValueChange = {},
                readOnly = true,
                label = {
                        Text("Selecciona el Gimnasio en el que entrenas")
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
                listaGimnasios.forEach { gym ->
                    DropdownMenuItem(
                        text = { Text(gym) },
                        onClick = {
                            gimnasioSeleccionado = gym
                            gimnasioExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        ExposedDropdownMenuBox(expanded = grupoExpanded, onExpandedChange = { grupoExpanded = !grupoExpanded }) {
            TextField(
                value = actividadFav,
                shape = RoundedCornerShape(24.dp),
                onValueChange = {},
                readOnly = true,
                label = {
                        Text("Actividad deportiva favorita")
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
                listaGrupos.forEach { grupo ->
                    DropdownMenuItem(
                        text = { Text(grupo) },
                        onClick = {
                            actividadFav = grupo
                            grupoExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        ExposedDropdownMenuBox(expanded = tiempoExpanded, onExpandedChange = { tiempoExpanded = !tiempoExpanded }) {
            TextField(
                value = tiempoEntrenando,
                shape = RoundedCornerShape(24.dp),
                onValueChange = {},
                readOnly = true,
                label = {
                        Text("Tiempo entrenado")
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
                tiempos.forEach { tiempo ->
                    DropdownMenuItem(
                        text = { Text(tiempo) },
                        onClick = {
                            tiempoEntrenando = tiempo
                            tiempoExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Foto de perfil (opcional)", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        // Imagen seleccionada (si existe)
        imagenUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Foto seleccionada",
                modifier = Modifier.size(120.dp).padding(8.dp)
            )
        }

        // Boton para seleccionar imagen de galería
        Button(
            onClick = { launcher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(containerColor = azulMarino)
        ) {
            Text("Seleccionar imagen desde galería")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para crear la cuenta en Firebase
        Button(
            onClick = {
                if (email.isNotBlank() && password.length >= 6) {
                    // Crear cuenta con Auth
                    auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                        .addOnSuccessListener { result ->
                            val uid = result.user?.uid ?: return@addOnSuccessListener
                            if (imagenUri != null) {
                                // Subir imagen a Storage
                                val storageRef = FirebaseStorage.getInstance().reference.child("fotos_perfil/$uid.jpg")
                                storageRef.putFile(imagenUri!!)
                                    .continueWithTask { task ->
                                        if (!task.isSuccessful) throw task.exception ?: Exception("Fallo al subir imagen")
                                        storageRef.downloadUrl
                                    }
                                    .addOnSuccessListener { uri ->
                                        guardarUsuarioConFoto(uid, uri.toString())
                                    }
                                    .addOnFailureListener {
                                        errorMessage = "Error al subir la imagen: ${it.message}"
                                    }
                            } else {
                                guardarUsuarioConFoto(uid, null)
                            }
                        }
                        .addOnFailureListener {
                            errorMessage = "Error al crear cuenta: ${it.message}"
                        }
                } else {
                    errorMessage = "Completa todos los campos y usa una contraseña válida (mínimo 6 caracteres)."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = azulMarino)
        ) {
            Text("Crear cuenta")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
