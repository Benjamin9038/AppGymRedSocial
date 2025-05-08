package com.example.poyectofinalcompose.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.poyectofinalcompose.ui.screens.LoginScreen
import com.example.poyectofinalcompose.ui.screens.UserScreen
import com.example.poyectofinalcompose.ui.screens.PantallaGimnasios
import com.example.poyectofinalcompose.ui.screens.PantallaUsuariosPorGimnasio
import com.example.poyectofinalcompose.ui.screens.PantallaChat

// Definición de pantallas válidas
open class Screen(val route: String) {
    object Login : Screen("login")
    object User : Screen("user")
    object Gym : Screen("gimnasios")

    object UsuariosPorGimnasio : Screen("usuariosPorGimnasio/{gymId}") {
        fun createRoute(gymId: String) = "usuariosPorGimnasio/$gymId"
    }

    object Chat : Screen("chat/{receptorUid}/{receptorNombre}") {
        fun createRoute(receptorUid: String, receptorNombre: String) =
            "chat/$receptorUid/$receptorNombre"
    }
}

// Configuración de navegación
@Composable
fun NavGraph(navController: NavHostController, isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.User.route) {
            UserScreen(navController)
        }

        composable(Screen.Gym.route) {
            PantallaGimnasios(navController)
        }

        composable(Screen.UsuariosPorGimnasio.route) { backStackEntry ->
            val gymId = backStackEntry.arguments?.getString("gymId") ?: "desconocido"
            PantallaUsuariosPorGimnasio(navController, gymId)
        }

        composable(Screen.Chat.route) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("receptorUid") ?: ""
            val nombre = backStackEntry.arguments?.getString("receptorNombre") ?: ""
            PantallaChat(navController, uid, nombre)
        }
    }
}
