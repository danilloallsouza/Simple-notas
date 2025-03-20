package com.example.simplenotas.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenotas.data.model.Note
import com.example.simplenotas.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Date

class NotesViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _previewSize = MutableStateFlow(48)
    val previewSize: StateFlow<Int> = _previewSize

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            when {
                !searchQuery.value.isNullOrEmpty() -> {
                    repository.searchNotes(searchQuery.value)
                }
                selectedCategory.value != null -> {
                    repository.getNotesByCategory(selectedCategory.value!!)
                }
                else -> {
                    repository.getAllNotes()
                }
            }.catch { e ->
                // Handle error
            }.collect { notesList ->
                _notes.value = notesList
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        loadNotes()
    }

    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
        loadNotes()
    }

    fun createNote(title: String, content: String, category: String = "") {
        viewModelScope.launch {
            // Cores mais vibrantes para melhor visibilidade
            val colors = listOf(
                0xFFE53935, // Vermelho mais vibrante
                0xFFD81B60, // Rosa mais vibrante
                0xFF8E24AA, // Roxo mais vibrante
                0xFF5E35B1, // Roxo escuro mais vibrante
                0xFF3949AB, // Índigo mais vibrante
                0xFF1E88E5, // Azul mais vibrante
                0xFF039BE5, // Azul claro mais vibrante
                0xFF00ACC1, // Ciano mais vibrante
                0xFF00897B, // Verde-azulado mais vibrante
                0xFF43A047, // Verde mais vibrante
                0xFF7CB342, // Verde claro mais vibrante
                0xFFFB8C00, // Laranja mais vibrante
                0xFFF4511E  // Coral mais vibrante
            )
            
            // Escolher uma cor aleatória para a nova nota
            val randomColor = colors.random()
            
            val note = Note(
                title = title,
                content = content,
                category = category,
                createdAt = Date(),
                modifiedAt = Date(),
                backgroundColor = randomColor
            )
            repository.insertNote(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note.copy(
                modifiedAt = Date(),
                reminderDate = note.reminderDate,
                backgroundColor = note.backgroundColor
            ))
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun shareNote(note: Note): String {
        return "${note.title}\n\n${note.content}"
    }
    
    fun duplicateNote(note: Note) {
        viewModelScope.launch {
            // Cores mais vibrantes para melhor visibilidade
            val colors = listOf(
                0xFFE53935, // Vermelho mais vibrante
                0xFFD81B60, // Rosa mais vibrante
                0xFF8E24AA, // Roxo mais vibrante
                0xFF5E35B1, // Roxo escuro mais vibrante
                0xFF3949AB, // Índigo mais vibrante
                0xFF1E88E5, // Azul mais vibrante
                0xFF039BE5, // Azul claro mais vibrante
                0xFF00ACC1, // Ciano mais vibrante
                0xFF00897B, // Verde-azulado mais vibrante
                0xFF43A047, // Verde mais vibrante
                0xFF7CB342, // Verde claro mais vibrante
                0xFFFB8C00, // Laranja mais vibrante
                0xFFF4511E  // Coral mais vibrante
            )
            
            // Escolher uma cor aleatória diferente da original
            var newColor = colors.random()
            if (note.backgroundColor == newColor) {
                // Se a cor for igual à original, escolher outra
                newColor = colors.filter { it != note.backgroundColor }.random()
            }
            
            val duplicatedNote = Note(
                title = "${note.title} (Cópia)",
                content = note.content,
                category = note.category,
                isFormatted = note.isFormatted,
                createdAt = Date(),
                modifiedAt = Date(),
                reminderDate = null,
                isProtected = note.isProtected,
                backgroundColor = newColor
            )
            repository.insertNote(duplicatedNote)
        }
    }

    fun increasePreviewSize() {
        _previewSize.value = (_previewSize.value + 24).coerceAtMost(200)
    }

    fun decreasePreviewSize() {
        _previewSize.value = (_previewSize.value - 24).coerceAtLeast(24)
    }
}