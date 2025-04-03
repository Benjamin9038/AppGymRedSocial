package com.example.poyectofinalcompose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.poyectofinalcompose.Data.Model.Usuario
import com.example.poyectofinalcompose.Data.Repository.UserRepository
import com.example.poyectofinalcompose.Navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(navController: NavController) {
    //Variables de Firebase Auth y el repositorio para guardar usuario
    val auth = FirebaseAuth.getInstance()
    val userRepository = remember { UserRepository() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("Masculino") }

    var masculino by remember { mutableStateOf(true) }
    var femenino by remember { mutableStateOf(false) }

    // Listado y selección de gimnasio
    val listaGimnasios = listOf(
        "Basic Fit Albacete", "McFit Albacete", "Fitness Villarrobledo",
        "Tiger Villarrobledo", "FraileGym Villarrobledo", "Centro Albacete", "Otro"
    )
    var gimnasioSeleccionado by remember { mutableStateOf(listaGimnasios[0]) }
    var gimnasioExpanded by remember { mutableStateOf(false) }

    val listaGrupos = listOf("Pecho", "Espalda", "Pierna", "Hombro", "Bíceps", "Tríceps", "Abdominales")
    var grupoMuscular by remember { mutableStateOf(listaGrupos[0]) }
    var grupoExpanded by remember { mutableStateOf(false) }

    val tiempos = listOf("menos de 6 meses", "6-12 meses", "1-3 años", "más de 3 años")
    var tiempoEntrenando by remember { mutableStateOf(tiempos[0]) }
    var tiempoExpanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    // Contenedor principal con scroll vertical
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear Cuenta", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Campos del formulario
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            //para que no salgan los caracteres de la contraseña
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = edad,
            onValueChange = { edad = it },
            label = { Text("Edad") },
            //para que en el teclado solo salgan numeros
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = peso,
            onValueChange = { peso = it },
            label = { Text("Peso (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = altura,
            onValueChange = { altura = it },
            label = { Text("Altura (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text("Género", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = masculino, onCheckedChange = { isChecked ->
                masculino = isChecked
                femenino = !isChecked
                genero = "Masculino"
            })
            Text("Masculino")
            Spacer(modifier = Modifier.width(16.dp))
            Checkbox(checked = femenino, onCheckedChange = {isChecked ->
                femenino = isChecked
                masculino = !isChecked
                genero = "Femenino"
            })
            Text("Femenino")
        }

        // Dropdown de gimnasio
        Spacer(modifier = Modifier.height(16.dp))
        Text("Selecciona tu gimnasio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        ExposedDropdownMenuBox(expanded = gimnasioExpanded, onExpandedChange = { gimnasioExpanded = !gimnasioExpanded }) {
            TextField(
                value = gimnasioSeleccionado,
                onValueChange = {},
                readOnly = true, //Para que el usuario no pueda editar nada
                label = { Text("Gimnasio") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.menuAnchor()//indica que el Dropdown se mostrará justo debajo del TextField
                    .fillMaxWidth()
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

        // Dropdown de grupo muscular favorito
        Spacer(modifier = Modifier.height(16.dp))
        Text("Grupo muscular favorito", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        ExposedDropdownMenuBox(expanded = grupoExpanded, onExpandedChange = { grupoExpanded = !grupoExpanded }) {
            TextField(
                value = grupoMuscular,
                onValueChange = {},
                readOnly = true,
                label = { Text("Grupo muscular") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            DropdownMenu(expanded = grupoExpanded, onDismissRequest = { grupoExpanded = false }) {
                listaGrupos.forEach { grupo ->
                    DropdownMenuItem(
                        text = { Text(grupo) },
                        onClick = {
                            grupoMuscular = grupo
                            grupoExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Tiempo entrenando", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        ExposedDropdownMenuBox(expanded = tiempoExpanded, onExpandedChange = { tiempoExpanded = !tiempoExpanded }) {
            TextField(
                value = tiempoEntrenando,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tiempo entrenando") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.menuAnchor().fillMaxWidth()
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

        Button(
            onClick = {
                if (email.isNotBlank() && password.length >= 6) {
                    // Intentar crear cuenta en Firebase Authentication con email y contraseña
                    auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                        .addOnSuccessListener { result ->
                            // Obtener el UID generado por Firebase para este nuevo usuario
                            val uid = result.user?.uid ?: return@addOnSuccessListener

                            //crea un objeto Usuario con los datos introducidos en el formulario
                            val nuevoUsuario = Usuario(
                                uid = uid,
                                email = email,
                                nombre = nombre,
                                edad = edad.toIntOrNull() ?: 0,
                                peso = peso.toDoubleOrNull() ?: 0.0,
                                altura = altura.toDoubleOrNull() ?: 0.0,
                                genero = genero,
                                gymId = gimnasioSeleccionado,
                                grupoMuscularFavorito = grupoMuscular,
                                tiempoEntrenando = tiempoEntrenando
                            )

                            //Guarda el usuario en Firestore a través del repositorio
                            userRepository.guardarUsuario(nuevoUsuario) { success, error ->
                                if (success) {
                                    // si se ha guardado correctamente, inicia sesion con el nuevo usuario
                                    auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                        .addOnSuccessListener {
                                            navController.navigate(Screen.Gym.route)
                                        }
                                        .addOnFailureListener {
                                            errorMessage = "Cuenta creada pero fallo al iniciar sesión: ${it.message}"
                                        }
                                } else {
                                    errorMessage = "Error al guardar datos: $error"
                                }
                            }
                        }
                        .addOnFailureListener {
                            errorMessage = "Error al crear cuenta: ${it.message}"
                        }
                } else {
                    errorMessage = "Completa todos los campos y usa una contraseña válida (mínimo 6 caracteres)."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear cuenta")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
