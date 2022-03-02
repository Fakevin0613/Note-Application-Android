package com.noteapplication.cs398

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    val allNotes: LiveData<List<Note>>
    private val dao: NoteDataAccess

    init{
        dao = NoteDatabase.getDatabase(application).getNoteDataAccess()
        allNotes = dao.getNotes()
    }

    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        dao.update(note)
    }

    fun insertNote(note: Note, tags: Array<Tag>? = null) = viewModelScope.launch(Dispatchers.IO) {
        dao.insert(note)
        tags?.let {

        }
    }
}