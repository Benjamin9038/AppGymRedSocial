package com.example.poyectofinalcompose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.poyectofinalcompose.Navigation.Screen

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar sesión o crear cuenta", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // BOTÓN INICIAR SESIÓN
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }



        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                navController.navigate(Screen.User.route)
            }
        ) {
            Text("¿No tienes cuenta? Crear cuenta")
        }



        if (errorMessage.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(errorMessage, color = MaterialTheme.colorScheme.error)
                        }
                    }
            }



