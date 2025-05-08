package com.example.poyectofinalcompose.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun PantallaChat(navController: NavController, receptorUid: String, receptorNombre: String) {
    Text(text = "Chat con $receptorNombre (UID: $receptorUid)")
}
