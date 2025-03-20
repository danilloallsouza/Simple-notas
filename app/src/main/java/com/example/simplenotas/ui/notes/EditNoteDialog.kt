package com.example.simplenotas.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.input.pointer.pointerInput
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
    onConfirm: (title: String, content: String) -> Unit
) {
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }
    var isError by remember { mutableStateOf(false) }
    var contentBoxHeight by remember { mutableIntStateOf(200) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val scrollState = rememberScrollState()

                Text(
                    text = "Editar Nota",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
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
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Conteúdo") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = contentBoxHeight.dp)
                            .height(IntrinsicSize.Min)
                            .pointerInput(Unit) {
                                detectVerticalDragGestures { change, dragAmount ->
                                    change.consume()
                                    val delta = dragAmount / 3
                                    contentBoxHeight = (contentBoxHeight - delta).toInt().coerceIn(100, 500)
                                }
                            },
                        minLines = 5
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tamanho da caixa:", style = MaterialTheme.typography.labelSmall)
                        IconButton(
                            onClick = {
                                contentBoxHeight = (contentBoxHeight - 50).coerceIn(100, 500)
                            }
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Diminuir")
                        }
                        IconButton(
                            onClick = {
                                contentBoxHeight = (contentBoxHeight + 50).coerceIn(100, 500)
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Aumentar")
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
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
                                onConfirm(title, content)
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
