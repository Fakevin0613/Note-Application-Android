package com.noteapplication.cs398

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CourseViewModel(application: Application) : AndroidViewModel(application) {
    val allFolders: LiveData<List<Folder>>
    private val dao: NoteDataAccess

    init{
        dao = NoteDatabase.getDatabase(application).getNoteDataAccess()
        allFolders = dao.getFolders()
    }

    fun deleteNote(folder: Folder) = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(folder)
    }

    fun updateNote(folder: Folder) = viewModelScope.launch(Dispatchers.IO) {
        dao.update(folder)

        // need some way to detect deleted tags
//        tags?.let {_ ->
//            tags.forEach {
//                dao.insert(it)
//                dao.insert(TagNoteCrossRef(it.id, note.id))
//            }
//        }
    }

    fun insertFolder(folder: Folder) = viewModelScope.launch(Dispatchers.IO) {
        dao.insert(folder)
    }
}