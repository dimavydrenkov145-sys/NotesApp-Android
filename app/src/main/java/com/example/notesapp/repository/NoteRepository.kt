package com.example.notesapp.repository

import androidx.lifecycle.LiveData
import com.example.notesapp.data.local.dao.NoteDao
import com.example.notesapp.data.local.entity.Note

class NoteRepository(private val dao: NoteDao) {
    val allNotes: LiveData<List<Note>> = dao.getAllNotes()

    suspend fun insert(note: Note) = dao.insert(note)
    suspend fun delete(note: Note) = dao.delete(note)
}