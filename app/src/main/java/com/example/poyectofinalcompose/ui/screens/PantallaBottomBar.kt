package com.example.poyectofinalcompose.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.poyectofinalcompose.Navigation.BottomBarScreen


@Composable
fun PantallaBottomBar(navController: NavController) {

    // Controlador de navegación específico para las pantallas de la barra inferior
    val bottomNavController = rememberNavController()
    val screens = listOf(
        BottomBarScreen.Inicio,
        BottomBarScreen.Chats,
        BottomBarScreen.Configuracion
    )
    // Obtenemos la ruta actual para saber qué icono está activo en la barra inferior
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    Scaffold(
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icono, contentDescription = screen.titulo) },
                        label = { Text(screen.titulo) },
                        selected = currentRoute == screen.ruta,
                        onClick = {
                            // Si se pulsa el botón de inicio, se fuerza la navegación a esa ruta
                            if (screen.ruta == BottomBarScreen.Inicio.ruta) {
                                bottomNavController.popBackStack(BottomBarScreen.Inicio.ruta, inclusive = false)
                                bottomNavController.navigate(BottomBarScreen.Inicio.ruta) {
                                    launchSingleTop = true  // Evita duplicar la pantalla en el back stack(pila de pantallas o rutas)
                                }
                            } else {
                                // Para otras rutas se navega guardando estado
                                bottomNavController.navigate(screen.ruta) {
                                    popUpTo(bottomNavController.graph.findStartDestination().id) {
                                        saveState = true // Guarda el estado anterior (scroll)
                                    }
                                    launchSingleTop = true // No apila múltiples copias
                                    restoreState = true // Restaura el estado anterior si vuelve
                                }
                            }
                        }


                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomBarScreen.Inicio.ruta,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomBarScreen.Inicio.ruta) {
                PantallaGimnasios(bottomNavController)
            }
            composable(BottomBarScreen.Chats.ruta) {
                PantallaListaChats(
                    localNavController = bottomNavController,
                    globalNavController = navController //
                )
            }

            composable(BottomBarScreen.Configuracion.ruta) {
                PantallaConfiguracion(navController)
            }
            composable("usuariosPorGimnasio/{gymId}") { backStackEntry ->
                val gymId = backStackEntry.arguments?.getString("gymId") ?: ""
                PantallaUsuariosPorGimnasio(
                    bottomNavController = bottomNavController,
                    globalNavController = navController, //  este es el de NavGraph
                    gymId = gymId
                )
            }


        }
    }
}
