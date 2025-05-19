package com.example.poyectofinalcompose.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.poyectofinalcompose.Data.Model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaChats(navController: NavController) {
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    var usuariosConChat by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(currentUid) {
        if (currentUid == null) return@LaunchedEffect

        db.collection("chats")
            .get()
            .addOnSuccessListener { chatDocs ->
                val otrosUids = mutableSetOf<String>()

                for (doc in chatDocs) {
                    val ids = doc.id.split("_")
                    if (ids.contains(currentUid) && ids.size == 2) {
                        val otro = if (ids[0] == currentUid) ids[1] else ids[0]
                        otrosUids.add(otro)
                    }
                }

                if (otrosUids.isEmpty()) {
                    usuariosConChat = emptyList()
                    cargando = false
                    return@addOnSuccessListener
                }

                db.collection("users")
                    .whereIn("uid", otrosUids.toList())
                    .get()
                    .addOnSuccessListener { result ->
                        usuariosConChat = result.mapNotNull { it.toObject(Usuario::class.java) }
                        cargando = false
                    }
                    .addOnFailureListener {
                        cargando = false
                    }
            }
            .addOnFailureListener {
                cargando = false
            }
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tus Chats") })
        }
    ) { padding ->
        if (cargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (usuariosConChat.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes chats activos aún.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                ) {
                    items(usuariosConChat) { usuario ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    navController.navigate("chat/${usuario.uid}/${usuario.nombre}")
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                if (!usuario.fotoUrl.isNullOrEmpty()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(usuario.fotoUrl),
                                        contentDescription = "Foto",
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Sin foto",
                                        modifier = Modifier.size(48.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(usuario.nombre, fontWeight = FontWeight.Bold)
                                    Text("Toca para continuar el chat")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
