package com.noteapplication.cs398

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDataAccess: NoteDataAccess) {
    val allNotes: LiveData<List<Note>> = noteDataAccess.getNotes()
    suspend fun insert(note: Note){
        noteDataAccess.insert(note)
    }

    suspend fun delete(note: Note){
        noteDataAccess.delete(note)
    }

    suspend fun update(note: Note){
        noteDataAccess.update(note)
    }
}