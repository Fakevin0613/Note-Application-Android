package com.noteapplication.cs398

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.noteapplication.cs398.database.Folder
import com.noteapplication.cs398.database.NoteDataAccess
import com.noteapplication.cs398.database.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderViewModel(application: Application) : AndroidViewModel(application) {
    val allFolders: LiveData<List<Folder>>

    var isAddingFolder: Boolean = false
    private val dao: NoteDataAccess

    init{
        dao = NoteDatabase.getDatabase(application).getNoteDataAccess()
        allFolders = dao.getFolders()
    }

    fun deleteFolder(folder: Folder) = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(folder)
    }

    fun updateFolder(folder: Folder) = viewModelScope.launch(Dispatchers.IO) {
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