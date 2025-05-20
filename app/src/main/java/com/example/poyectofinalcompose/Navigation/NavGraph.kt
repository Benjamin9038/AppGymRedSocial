package com.example.poyectofinalcompose.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Message
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.poyectofinalcompose.ui.screens.LoginScreen
import com.example.poyectofinalcompose.ui.screens.PantallaBottomBar
import com.example.poyectofinalcompose.ui.screens.UserScreen
import com.example.poyectofinalcompose.ui.screens.PantallaGimnasios
import com.example.poyectofinalcompose.ui.screens.PantallaUsuariosPorGimnasio
import com.example.poyectofinalcompose.ui.screens.PantallaChat
import java.net.URLDecoder


// Definición de pantallas válidas
open class Screen(val route: String) {
    object Login : Screen("login")
    object User : Screen("user")
    object BottomBar : Screen("bottomBar")

    object UsuariosPorGimnasio : Screen("usuariosPorGimnasio/{gymId}") {
        fun createRoute(gymId: String) = "usuariosPorGimnasio/$gymId"
    }

    object Chat : Screen("chat/{receptorUid}/{receptorNombre}") {
        fun createRoute(receptorUid: String, receptorNombre: String) =
            "chat/$receptorUid/$receptorNombre"
    }
}

//Navegacion en la barra de abajo
sealed class BottomBarScreen(val ruta: String, val titulo: String, val icono: ImageVector) {
    object Inicio : BottomBarScreen("inicio", "Inicio", Icons.Default.Home)
    object Chats : BottomBarScreen("chats", "Chats", Icons.Default.Chat)
    object Configuracion : BottomBarScreen("configuracion", "Config", Icons.Default.Settings)
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

        composable(Screen.BottomBar.route) {
            PantallaBottomBar(navController)
        }

        composable(Screen.Chat.route) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("receptorUid") ?: ""
            val nombreEncoded = backStackEntry.arguments?.getString("receptorNombre") ?: ""
            val nombre = URLDecoder.decode(nombreEncoded, "UTF-8")
            PantallaChat(navController, uid, nombre)
        }


    }
}
