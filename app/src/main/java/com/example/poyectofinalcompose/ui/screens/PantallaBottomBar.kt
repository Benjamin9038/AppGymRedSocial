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
    val bottomNavController = rememberNavController()
    val screens = listOf(
        BottomBarScreen.Inicio,
        BottomBarScreen.Chats,
        BottomBarScreen.Configuracion
    )

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
                            bottomNavController.navigate(screen.ruta) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
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
                PantallaListaChats(bottomNavController)
            }
            composable(BottomBarScreen.Configuracion.ruta) {
                UserScreen(navController) // temporal, luego será PantallaConfiguracion
            }
            composable("usuariosPorGimnasio/{gymId}") { backStackEntry ->
                val gymId = backStackEntry.arguments?.getString("gymId") ?: ""
                PantallaUsuariosPorGimnasio(
                    bottomNavController = bottomNavController,
                    globalNavController = navController, // <- este es el de NavGraph
                    gymId = gymId
                )
            }


        }
    }
}
