package com.example.simplenotas.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.simplenotas.data.model.Note
import com.example.simplenotas.ui.components.ClickableTextContent

@Composable
fun EditNoteDialog(
    note: Note,
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }
    var category by remember { mutableStateOf(note.category) }
    var isError by remember { mutableStateOf(false) }
    var contentBoxHeight by remember { mutableIntStateOf(200) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Adicionando um ScrollState para permitir rolagem
                val scrollState = rememberScrollState()
                
                // Título do diálogo (fora da área de rolagem)
                // Título e espaçador já foram adicionados fora da área de rolagem
                
                // Conteúdo com rolagem
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                // Título e espaçador já foram adicionados fora da área de rolagem
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { 
                        title = it
                        isError = false
                    },
                    label = { Text("Título") },
                    isError = isError,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                if (isError) {
                    Text(
                        text = "O título não pode estar vazio",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Column(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("Conteúdo") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(contentBoxHeight.dp),
                            minLines = 5
                        )
                        
                        // Controles para aumentar/diminuir o tamanho da caixa de conteúdo
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tamanho da caixa:", style = MaterialTheme.typography.labelSmall)
                            IconButton(
                                onClick = { contentBoxHeight = (contentBoxHeight - 50).coerceAtLeast(100) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Filled.Remove, contentDescription = "Diminuir tamanho")
                            }
                            IconButton(
                                onClick = { contentBoxHeight = (contentBoxHeight + 50).coerceAtMost(500) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Aumentar tamanho")
                            }
                        }
                    }
                    
                    // Visualização do conteúdo com links e telefones clicáveis
                    if (content.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            tonalElevation = 1.dp,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "Prévia:",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                ClickableTextContent(
                                    text = content,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoria") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                } // Fim da coluna com rolagem
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Botões fora da área de rolagem para garantir que estejam sempre visíveis
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                isError = true
                            } else {
                                onConfirm(title, content, category)
                                onDismiss()
                            }
                        }
                    ) {
                        Text("Salvar")
                    }
                }
            }
        }
    }
}