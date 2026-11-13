package com.example.notesapp.ui.viewmodel

import com.example.notesapp.data.local.entity.Note

data class NotesUiState(
    val isLoading: Boolean = false,
    val notes: List<Note> = emptyList(),
    val errorMessage: String? = null
)