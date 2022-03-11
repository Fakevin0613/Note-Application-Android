package com.noteapplication.cs398

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TagViewModel(application: Application) : AndroidViewModel(application) {
    var allTags: LiveData<List<Tag>>
    var selectedTagIds = mutableSetOf<Long>()

    private val dao: NoteDataAccess

    init{
        dao = NoteDatabase.getDatabase(application).getNoteDataAccess()
        allTags = dao.getTags()
    }

    fun getSelectedTags(): List<Tag>{
        return selectedTagIds.map { id ->
            val item = allTags.value?.find { it.id == id }
            assert(item != null) // Null tag selected
            item!!
        }
    }

    fun setCurrentSelectedTags(noteId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            dao.getTags(noteId)
                .map {it.id}
                .toSet()
                .let {selectedTagIds.addAll(it)}
        }
    }

//    fun updateTags(note: Note){
//        allTags = dao.getTags(note.id)
//    }

    fun deleteTag(tag: Tag) = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(tag)
    }

    fun insertTag(tag: Tag) = viewModelScope.launch(Dispatchers.IO) {
        selectedTagIds.add(dao.insert(tag))
    }
}
