package com.example.simplenotas.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.simplenotas.data.model.Note
import com.example.simplenotas.ui.components.ClickableTextContent
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    onNoteClick: (Note) -> Unit,
    onCreateNote: () -> Unit,
    onShareNote: (Note) -> Unit
) {
    val notes by viewModel.notes.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    val previewSize by viewModel.previewSize.collectAsState()
    var isSearchVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simple Notas") },
                actions = {
                    IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNote) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (isSearchVisible || searchQuery.isNotEmpty()) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::setSearchQuery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes) { note ->
                    NoteCard(
                        note = note,
                        previewSize = previewSize,
                        onClick = { onNoteClick(note) },
                        onDelete = { viewModel.deleteNote(note) },
                        onShare = { onShareNote(note) },
                        onDuplicate = { viewModel.duplicateNote(note) },
                        onIncreasePreview = { viewModel.increasePreviewSize() },
                        onDecreasePreview = { viewModel.decreasePreviewSize() }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search notes...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true
    )
}

@Composable
fun NoteCard(
    note: Note,
    previewSize: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onDuplicate: () -> Unit,
    onIncreasePreview: () -> Unit,
    onDecreasePreview: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    // Definir a cor de fundo com base no backgroundColor da nota
    val backgroundColor = note.backgroundColor?.let { Color(it.toInt()) } ?: MaterialTheme.colorScheme.surface

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.Share, contentDescription = "Share note")
                    }
                    IconButton(onClick = onDuplicate) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Duplicate note")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete note")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Usando o componente ClickableTextContent para exibir o conteúdo com links e telefones clicáveis
            Box(modifier = Modifier.height(previewSize.dp)) {
                ClickableTextContent(
                    text = note.content,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Controles para aumentar/diminuir o tamanho da prévia
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onDecreasePreview,
                    modifier = Modifier.size(24.dp)
                ) {
                    Text("-", style = MaterialTheme.typography.labelLarge)
                }
                IconButton(
                    onClick = onIncreasePreview,
                    modifier = Modifier.size(24.dp)
                ) {
                    Text("+", style = MaterialTheme.typography.labelLarge)
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Modified: ${dateFormat.format(note.modifiedAt)}",
                style = MaterialTheme.typography.bodySmall
            )
            if (note.category.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.category,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}