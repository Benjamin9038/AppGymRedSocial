package com.example.poyectofinalcompose.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.poyectofinalcompose.Data.Model.calcularNota
import com.example.poyectofinalcompose.Data.Repository.PruebaRepository

@OptIn(ExperimentalMaterial3Api::class) // Esto es por el uso de TopAppBar, que es experimental
@Composable
fun DetalleScreen(pruebaId: String, navController: NavController, edad: Int, genero: String) {
    var resultado by rememberSaveable { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }
    val dialog = remember { mutableStateOf(false) }

    val pruebaRepository = PruebaRepository()
    val pruebaIdInt = pruebaId.toIntOrNull() // Convierte el ID de la prueba a Int
    val prueba = remember { pruebaRepository.obtenerPruebas().find { it.id == pruebaIdInt } } // Busca la prueba por ID

    Scaffold(
        topBar = {
            //Centrar el título de la prueba
            TopAppBar(
                title = {
                    Text(
                        text = if (prueba != null) prueba.nombre else "Prueba Desconocida",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Mostrar imagen de la prueba si existe
            prueba?.let {
                Image(
                    painter = painterResource(id = it.imagen),
                    contentDescription = it.nombre,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Texto centrado "Introduce los datos..."
            Text(
                text = "Introduce los datos para la prueba",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Campo de entrada para el resultado (sin nombre de prueba encima)
            OutlinedTextField(
                value = resultado,
                onValueChange = { resultado = it },
                label = { Text("Ingrese resultado") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Botón para calcular la nota
            Button(
                onClick = {
                    val resultadoNum = resultado.toFloatOrNull() // Intenta convertir el input a Float
                    if (resultadoNum != null && prueba != null) {
                        val nombreLimpio = prueba.nombre.trim().lowercase().replace("''", "") // Limpieza del nombre
                        val generoLimpio = genero.trim().replaceFirstChar { it.uppercaseChar() } // Normalización del género
                        // DEPURACIÓN: Mostrar valores en Logcat
                        println("  Calculando nota para:")
                        println(" - Prueba: '$nombreLimpio'")
                        println(" - Resultado ingresado: $resultadoNum")
                        println(" - Edad: $edad")
                        println(" - Género: '$generoLimpio'")

                        // Calcula la nota
                        nota = calcularNota(nombreLimpio, resultadoNum, edad, generoLimpio)

                        println(" - Nota Calculada: $nota")
                        dialog.value = true // Muestra el diálogo con el resultado
                    } else {
                        nota = "Entrada inválida" // Mensaje de error si el input es incorrecto
                    }
                }
            ) {
                Text("Calcular Nota")
            }
        }
    }

    //Diálogo que muestra el resultado de la nota calculada
    if (dialog.value) {
        AlertDialog(
            onDismissRequest = { dialog.value = false },
            confirmButton = {
                Button(onClick = { dialog.value = false }) {
                    Text("Aceptar")
                }
            },
            title = { Text("Resultado") },
            text = { Text("Tu nota es: $nota") }
        )
    }
}
