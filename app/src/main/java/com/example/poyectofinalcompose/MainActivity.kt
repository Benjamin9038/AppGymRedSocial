package com.example.poyectofinalcompose

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.poyectofinalcompose.Navigation.NavGraph
import com.example.poyectofinalcompose.ui.theme.PoyectoFinalComposeTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔍 Comprobar la orientación en onCreate
        checkOrientation()

        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            val navController = rememberNavController()

            PoyectoFinalComposeTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {},
                            actions = {
                                Switch(
                                    checked = isDarkTheme,
                                    onCheckedChange = { isDarkTheme = it },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavGraph(navController, isDarkTheme) { isDarkTheme = it }
                    }
                }
            }
        }
    }

    //Función para verificar la orientación del dispositivo
    //(No es necesario pero no podia girar la pantalla y tuve que usarlo para ver el error)
    private fun checkOrientation() {
        val orientation = resources.configuration.orientation
        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> Log.d("OrientationCheck", "Modo Vertical (Portrait)")
            Configuration.ORIENTATION_LANDSCAPE -> Log.d("OrientationCheck", "Modo Horizontal (Landscape)")
            else -> Log.d("OrientationCheck", "Modo Desconocido")
        }
    }
}
