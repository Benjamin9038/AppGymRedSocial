package com.example.poyectofinalcompose.Navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.poyectofinalcompose.ui.screens.*
import com.example.poyectofinalcompose.ui.screens.PantallaGimnasios


 //Definición de las pantallas disponibles en la aplicación.
open class Screen(val route: String) {
    object Login : Screen("login")
    object User : Screen("user")
     object Pruebas : Screen("pruebas/{edad}/{genero}") {
        fun createRoute(edad: Int, genero: String) = "pruebas/$edad/$genero"
    }
    object Detalle : Screen("detalle/{pruebaId}/{edad}/{genero}") { // Pantalla de detalle de prueba
        fun createRoute(pruebaId: String, edad: Int, genero: String) = "detalle/$pruebaId/$edad/$genero"
    }
     object Gym : Screen("gimnasios")

 }


 //Configuración de la navegación entre las pantallas de la aplicación.
@Composable
fun NavGraph(navController: NavHostController, isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    var edad by rememberSaveable { mutableStateOf(0) } // Estado para almacenar la edad del usuario
    var genero by rememberSaveable { mutableStateOf("Masculino") } // Estado para almacenar el género del usuario

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route // Se inicia en la pantalla de Login
    ) {
        // Pantalla de inicio de sesión
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        // Pantalla de datos del usuario
        composable(Screen.User.route) {
            UserScreen(navController)
        }


        // Pantalla de pruebas según edad y género
        composable(Screen.Pruebas.route) { backStackEntry ->
            val edadString = backStackEntry.arguments?.getString("edad")
            val generoString = backStackEntry.arguments?.getString("genero")

            // Verifica si la edad es válida, si no, asigna 0
            val edad: Int
            if (edadString != null) {
                val parsedEdad = edadString.toIntOrNull()
                if (parsedEdad != null) {
                    edad = parsedEdad
                } else {
                    edad = 0
                }
            } else {
                edad = 0
            }

            // Verifica si el género es válido, si no, asigna "Masculino"
            val genero: String
            if (generoString != null) {
                genero = generoString
            } else {
                genero = "Masculino"
            }


        }

        // Pantalla de detalle de la prueba (usando operador Elvis para un código más limpio)
        composable(Screen.Detalle.route) { backStackEntry ->
            val pruebaId = backStackEntry.arguments?.getString("pruebaId") ?: ""
            val edad = backStackEntry.arguments?.getString("edad")?.toIntOrNull() ?: 0
            val genero = backStackEntry.arguments?.getString("genero") ?: "Masculino"
            DetalleScreen(pruebaId, navController, edad, genero)
        }

        composable(Screen.Gym.route) {
            PantallaGimnasios(navController)
        }
    }
}
