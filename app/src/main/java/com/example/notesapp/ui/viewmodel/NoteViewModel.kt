package com.example.notesapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.local.NoteDatabase
import com.example.notesapp.data.local.entity.Note
import com.example.notesapp.domain.usecase.AddNoteUseCase
import com.example.notesapp.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    private val addNoteUseCase: AddNoteUseCase

    // Приватное изменяемое состояние (UDF)
    private val _uiState = MutableStateFlow(NotesUiState())
    // Публичное неизменяемое состояние для UI
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        val dao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(dao)
        addNoteUseCase = AddNoteUseCase(repository)

        // Подписываемся на LiveData из Room и превращаем в StateFlow
        viewModelScope.launch {
            repository.allNotes.observeForever { notes ->
                _uiState.update { currentState ->
                    currentState.copy(notes = notes)
                }
            }
        }
    }

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Вызываем UseCase
            val result = addNoteUseCase(title, content)

            result.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message, isLoading = false) }
            }.onSuccess {
                _uiState.update { it.copy(errorMessage = null, isLoading = false) }
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}