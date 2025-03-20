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
import com.example.simplenotas.ui.notes.ViewNoteScreen
import com.example.simplenotas.ui.theme.SimpleNotasTheme
import com.example.simplenotas.ui.notes.LegalScreen

@Composable
fun AppNavigation(
    viewModel: NotesViewModel,
    onShareNote: (String) -> Unit
) {
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showEditNoteDialog by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var showViewNoteScreen by remember { mutableStateOf(false) }
    var showLegalScreen by remember { mutableStateOf(false) }
    var legalScreenContent by remember { mutableStateOf<Pair<String, String>?>(null) }

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
                onConfirm = { title, content ->
                    viewModel.updateNote(note.copy(
                        title = title,
                        content = content,
                        backgroundColor = note.backgroundColor
                    ))
                    showEditNoteDialog = false
                    selectedNote = null
                }
            )
        }
    }

    if (showLegalScreen && legalScreenContent != null) {
        Surface(modifier = Modifier.fillMaxSize()) {
            LegalScreen(
                title = legalScreenContent!!.first,
                content = legalScreenContent!!.second,
                onBackClick = {
                    showLegalScreen = false
                    legalScreenContent = null
                    showViewNoteScreen = true
                }
            )
        }
    } else if (showViewNoteScreen && selectedNote != null) {
        ViewNoteScreen(
            note = selectedNote!!,
            onBackClick = {
                showViewNoteScreen = false
                selectedNote = null
            },
            onShareClick = {
                val shareText = viewModel.shareNote(selectedNote!!)
                onShareNote(shareText)
            },
            onDeleteClick = {
                viewModel.deleteNote(selectedNote!!)
                showViewNoteScreen = false
                selectedNote = null
            },
            onSaveEditClick = { title, content ->
                viewModel.updateNote(selectedNote!!.copy(
                    title = title,
                    content = content
                ))
                selectedNote = selectedNote!!.copy(
                    title = title,
                    content = content
                )
            }
        )
    } else {
        NotesScreen(
            viewModel = viewModel,
            onNoteClick = { note ->
                selectedNote = note
                showViewNoteScreen = true
            },
            onCreateNote = { showAddNoteDialog = true },
            onShareNote = { note ->
                onShareNote(viewModel.shareNote(note))
            },
            onLegalScreenOpen = { title, content ->
                legalScreenContent = Pair(title, content)
                showViewNoteScreen = false
                showLegalScreen = true
            }
        )
    }
}

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
                    AppNavigation(
                        viewModel = viewModel,
                        onShareNote = { noteContent ->
                            shareNote(noteContent)
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