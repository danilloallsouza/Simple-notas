package com.example.simplenotas.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.simplenotas.data.model.Note
import com.example.simplenotas.ui.components.ClickableTextContent
import java.text.SimpleDateFormat
import java.util.*

/**
 * Tela de visualização completa de uma nota
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewNoteScreen(
    note: Note,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSaveEditClick: (String, String) -> Unit // Callback para salvar as edições
) {
    val backgroundColor = note.backgroundColor?.let { Color(it.toLong()) } ?: MaterialTheme.colorScheme.surface
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    // Definir a cor do texto: cinza escuro para fundo amarelo, padrão do tema para outros casos
    val textColor = if (backgroundColor == Color.Yellow) Color.DarkGray else MaterialTheme.colorScheme.onSurface

    // Estado para controlar o menu de opções
    var showMenu by remember { mutableStateOf(false) }

    // Estado para controlar a visibilidade do layout de edição
    var showEditLayout by remember { mutableStateOf(false) }

    // Estados para o título e conteúdo editados
    var editedTitle by remember { mutableStateOf(note.title) }
    var editedContent by remember { mutableStateOf(note.content) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        note.title,
                        color = textColor // Ajustar cor do título
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditLayout = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar nota")
                    }

                    Box {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Mais opções")
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Compartilhar", color = textColor) }, // Ajustar cor
                                onClick = {
                                    onShareClick()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Share, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Excluir", color = textColor) }, // Ajustar cor
                                onClick = {
                                    onDeleteClick()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Informações de data com cor ajustada
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Criado em: ${dateFormat.format(note.createdAt)}",
                    style = MaterialTheme.typography.bodySmall.copy(color = textColor)
                )
                Text(
                    text = "Modificado em: ${dateFormat.format(note.modifiedAt)} às ${timeFormat.format(note.modifiedAt)}",
                    style = MaterialTheme.typography.bodySmall.copy(color = textColor)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Conteúdo da nota (visível apenas quando não estiver editando)
            if (!showEditLayout) {
                ClickableTextContent(
                    text = note.content,
                    modifier = Modifier.fillMaxWidth(),
                    textColor = textColor // Passar a cor do texto
                )
            }

            // Layout de edição
            if (showEditLayout) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    OutlinedTextField(
                        value = editedTitle,
                        onValueChange = { editedTitle = it },
                        label = { Text("Título", color = textColor) }, // Ajustar cor do label
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = TextStyle(color = textColor) // Ajustar cor do texto
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = editedContent,
                        onValueChange = { editedContent = it },
                        label = { Text("Conteúdo", color = textColor) }, // Ajustar cor do label
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        textStyle = TextStyle(color = textColor) // Ajustar cor do texto
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                showEditLayout = false
                                editedTitle = note.title
                                editedContent = note.content
                            }
                        ) {
                            Text("Cancelar", color = textColor) // Ajustar cor
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(
                            onClick = {
                                if (editedTitle.isNotBlank() && editedContent.isNotBlank()) {
                                    onSaveEditClick(editedTitle, editedContent)
                                    showEditLayout = false
                                }
                            }
                        ) {
                            Text("Salvar", color = textColor) // Ajustar cor
                        }
                    }
                }
            }
        }
    }
}