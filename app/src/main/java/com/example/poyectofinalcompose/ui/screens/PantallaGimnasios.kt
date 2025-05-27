package com.example.poyectofinalcompose.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.poyectofinalcompose.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGimnasios(navController: NavController){
    data class Gimnasio(
        val nombre: String,
        val imagenResId: Int
    )

    val gimnasios = listOf(
        Gimnasio("Basic Fit Albacete", R.drawable.basicfit),
        Gimnasio("McFit Albacete", R.drawable.mcfit),
        Gimnasio("Centro Albacete", R.drawable.centro),
        Gimnasio("AltaFit Albacete", R.drawable.altafit),
        Gimnasio("Fitness Villarrobledo", R.drawable.fitness),
        Gimnasio("Tiger Villarrobledo", R.drawable.tiger),
        Gimnasio("FraileGym Villarrobledo", R.drawable.fraile)
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
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)

        ) {
            items(gimnasios) { gimnasio ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate("usuariosPorGimnasio/${gimnasio.nombre}")
                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = gimnasio.imagenResId),
                            contentDescription = gimnasio.nombre,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        )
                        Text(
                            text = gimnasio.nombre,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}
