package com.example.poyectofinalcompose.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.poyectofinalcompose.Data.Model.Prueba
import com.example.poyectofinalcompose.Data.Repository.PruebaRepository
import com.example.poyectofinalcompose.Baremos.Baremos
import com.example.poyectofinalcompose.Navigation.Screen
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PruebasScreen(navController: NavController, edad: Int, genero: String) {
    //Obtener la lista de pruebas desde el repositorio
    val todasLasPruebas = remember { PruebaRepository().obtenerPruebas() }

    //Estado para la búsqueda y la categoría seleccionada
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    val categorias = listOf("Todas", "Fuerza", "Flexibilidad", "Velocidad", "Agilidad", "Coordinación", "Resistencia")

    //Filtrar las pruebas disponibles según edad, categoría y búsqueda
    val pruebasDisponibles = remember(searchQuery.text, categoriaSeleccionada, edad) {
        todasLasPruebas.filter { prueba ->
            Baremos.obtenerPruebasPorEdad(edad).contains(prueba.nombre) &&
                    (searchQuery.text.isEmpty() || prueba.nombre.contains(searchQuery.text, ignoreCase = true)) &&
                    (categoriaSeleccionada == "Todas" || prueba.categoria.split(", ").contains(categoriaSeleccionada))
        }
    }

    Scaffold(
        topBar = {
            Column {
                //Barra superior con el título de la pantalla
                TopAppBar(
                    title = { Text("Pruebas para $edad años - $genero") }
                )

                //Barra de búsqueda
                SearchView(searchQuery) { newQuery -> searchQuery = newQuery }

                //Menú desplegable para seleccionar categoría
                Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    var expanded by remember { mutableStateOf(false) }
                    Button(onClick = { expanded = true }) {
                        Text(categoriaSeleccionada)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(onClick = {
                                categoriaSeleccionada = categoria
                                expanded = false
                            }, text = { Text(categoria) })
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) //Permite desplazamiento vertical si hay muchas pruebas
        ) {
            //Si no hay pruebas disponibles, mostrar mensaje
            if (pruebasDisponibles.isEmpty()) {
                Text("No hay pruebas disponibles para esta edad o búsqueda.")
            } else {
                //Mostrar cada prueba disponible
                pruebasDisponibles.forEach { prueba ->
                    PruebaItem(prueba, navController, edad, genero)
                }
            }
        }
    }
}

//Componente para la barra de búsqueda
@Composable
fun SearchView(query: TextFieldValue, onQueryChanged: (TextFieldValue) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = { onQueryChanged(it) },
        label = { Text("Buscar prueba por nombre") },
        singleLine = true, //Restringe la entrada a una sola línea
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
    )
}

//Componente para mostrar una prueba en forma de tarjeta
@Composable
fun PruebaItem(prueba: Prueba, navController: NavController, edad: Int, genero: String) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                //Navega a la pantalla de detalles al hacer clic en la tarjeta
                prueba.id?.let { id ->
                    navController.navigate(Screen.Detalle.createRoute(id.toString(), edad, genero))
                }
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp) //Sombra en la tarjeta
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Mostrar imagen de la prueba
            val imagen: Painter = painterResource(id = prueba.imagen)
            Image(
                painter = imagen,
                contentDescription = prueba.nombre,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                //Mostrar nombre y categoría de la prueba
                Text(text = prueba.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = prueba.categoria, style = MaterialTheme.typography.bodySmall)

                //Botón para ver video explicativo en YouTube
                TextButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(prueba.enlace))
                    context.startActivity(intent)
                }) {
                    Text("Aquí está un video explicativo de cómo hacerlo")
                }
            }
        }
    }
}


