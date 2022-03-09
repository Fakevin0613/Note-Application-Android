package com.noteapplication.cs398

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TagViewModel(application: Application) : AndroidViewModel(application) {
    var allTags: LiveData<List<Tag>>
    private val dao: NoteDataAccess

    init{
        dao = NoteDatabase.getDatabase(application).getNoteDataAccess()
        allTags = dao.getTags()
    }

    fun updateTags(note: Note){
        allTags = dao.getTags(note.id)
    }

    fun deleteTag(tag: Tag) = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(tag)
    }

    fun insertTag(tag: Tag) = viewModelScope.launch(Dispatchers.IO) {
        dao.insert(tag)
    }
}