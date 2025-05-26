package com.example.poyectofinalcompose.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGimnasios(navController: NavController){
    val azulMarino = Color(0xFF005A9C)

    val gimnasios = listOf(
        "Basic Fit Albacete",
        "McFit Albacete",
        "Centro Albacete",
        "AltaFit Albacete",
        "Fitness Villarrobledo",
        "Tiger Villarrobledo",
        "FraileGym Villarrobledo",
        "Otro"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "¿Donde quieres entrenar hoy?",
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold

                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(gimnasios) { gym ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(1.dp, azulMarino, shape = MaterialTheme.shapes.medium)
                        .clickable {
                            navController.navigate("usuariosPorGimnasio/${gym}")
                        }
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = gym,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}
