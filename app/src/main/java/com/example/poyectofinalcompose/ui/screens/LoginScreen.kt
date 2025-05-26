package com.example.poyectofinalcompose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.poyectofinalcompose.Navigation.Screen
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff


@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var passwordVisible by remember { mutableStateOf(false) }
    val azulMarino = androidx.compose.ui.graphics.Color(0xFF005A9C)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título centrado y en negrita
        Text(
            "Iniciar sesión en FitMate",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de correo electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Correo electrónico")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azulMarino,
                unfocusedBorderColor = azulMarino,
                focusedLabelColor = azulMarino,
                cursorColor = azulMarino
            )
        )


        Spacer(modifier = Modifier.height(8.dp))

        // Campo de contraseña

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    text = "Contraseña",
                    modifier = Modifier.padding(start = 100.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = description)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azulMarino,
                unfocusedBorderColor = azulMarino,
                focusedLabelColor = azulMarino,
                cursorColor = azulMarino
            )
        )



        Spacer(modifier = Modifier.height(16.dp))

        // BOTÓN INICIAR SESIÓN (con azul marino)
        Button(
            onClick = {
                // Inicia sesión en Firebase Auth con el correo y la contraseña que se ingresa
                auth.signInWithEmailAndPassword(email.trim(), password.trim())
                    .addOnSuccessListener { result ->
                        //obtenemos el uid del usuasrio
                        val uid = result.user?.uid ?: return@addOnSuccessListener

                        // Consultamos en Firestore si ese usuario tiene datos
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    // llevamos a la lista de gimnasios al ususario
                                    navController.navigate(Screen.BottomBar.route)
                                } else {
                                    // Usuario existe en Auth pero no tiene datos ir a completar perfil
                                    navController.navigate(Screen.User.route)
                                }
                            }
                            .addOnFailureListener {
                                // Fallo en Firestore
                                errorMessage = "Error al acceder a los datos del usuario."
                            }
                    }
                    .addOnFailureListener {
                        // Fallo de login (mal email o contraseña)
                        errorMessage = "Usuario o contraseña incorrectos. ¿Tienes cuenta creada?"
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = azulMarino)
        ) {
            Text("Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para ir a la pantalla de registro (también azul marino)
        TextButton(
            onClick = {
                navController.navigate(Screen.User.route)
            },
            colors = ButtonDefaults.textButtonColors(contentColor = azulMarino)
        ) {
            Text("¿No tienes cuenta? Crear cuenta")
        }

        // Mensaje de error si algo falla
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
