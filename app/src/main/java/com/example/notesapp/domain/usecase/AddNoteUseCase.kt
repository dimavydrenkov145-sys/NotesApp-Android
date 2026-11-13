package com.example.notesapp.domain.usecase

import com.example.notesapp.data.local.entity.Note
import com.example.notesapp.repository.NoteRepository

class AddNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(title: String, content: String): Result<Unit> {
        // Задание А: Валидация ввода
        if (title.isBlank()) {
            return Result.failure(IllegalArgumentException("Заголовок не может быть пустым"))
        }

        val note = Note(title = title, content = content)
        return try {
            repository.insert(note)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}