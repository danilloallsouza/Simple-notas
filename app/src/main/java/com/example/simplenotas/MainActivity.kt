package com.example.simplenotas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplenotas.data.local.NoteDatabase
import com.example.simplenotas.data.model.Note
import com.example.simplenotas.data.repository.NoteRepository
import com.example.simplenotas.ui.notes.AddNoteDialog
import com.example.simplenotas.ui.notes.EditNoteDialog
import com.example.simplenotas.ui.notes.NotesScreen
import com.example.simplenotas.ui.notes.NotesViewModel
import com.example.simplenotas.ui.notes.NotesViewModelFactory
import com.example.simplenotas.ui.theme.SimpleNotasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = NoteDatabase.getDatabase(applicationContext)
        val repository = NoteRepository(database.noteDao())

        setContent {
            SimpleNotasTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: NotesViewModel = viewModel(
                        factory = NotesViewModelFactory(repository)
                    )
                    
                    var showAddNoteDialog by remember { mutableStateOf(false) }
                    var showEditNoteDialog by remember { mutableStateOf(false) }
                    var selectedNote by remember { mutableStateOf<Note?>(null) }
                    
                    if (showAddNoteDialog) {
                        AddNoteDialog(
                            onDismiss = { showAddNoteDialog = false },
                            onConfirm = { title, content ->
                                viewModel.createNote(title, content)
                            }
                        )
                    }
                    
                    selectedNote?.let { note ->
                        if (showEditNoteDialog) {
                            EditNoteDialog(
                                note = note,
                                onDismiss = { 
                                    showEditNoteDialog = false 
                                    selectedNote = null
                                },
                                onConfirm = { title, content, category ->
                                    viewModel.updateNote(note.copy(
                                        title = title,
                                        content = content,
                                        category = category,
                                        backgroundColor = note.backgroundColor
                                    ))
                                }
                            )
                        }
                    }
                    
                    NotesScreen(
                        viewModel = viewModel,
                        onNoteClick = { note ->
                            selectedNote = note
                            showEditNoteDialog = true
                        },
                        onCreateNote = { showAddNoteDialog = true },
                        onShareNote = { note ->
                            shareNote(viewModel.shareNote(note))
                        }
                    )
                }
            }
        }
    }
    
    private fun shareNote(noteContent: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, noteContent)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Compartilhar nota")
        startActivity(shareIntent)
    }
}