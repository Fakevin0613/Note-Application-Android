package com.noteapplication.cs398

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NoteViewModel(application: Application, val folder: Folder? = null) : AndroidViewModel(application) {
    val allNotes: LiveData<List<Note>>
    private val dao: NoteDataAccess

    init{
        dao = NoteDatabase.getDatabase(application).getNoteDataAccess()
        allNotes = if(folder != null) dao.getNotesByFolderId(folderId = folder!!.id)
                   else dao.getNotes()
    }

    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        dao.update(note)

        // need some way to detect deleted tags
//        tags?.let {_ ->
//            tags.forEach {
//                dao.insert(it)
//                dao.insert(TagNoteCrossRef(it.id, note.id))
//            }
//        }
    }

    fun insertNote(note: Note, tags: Array<Tag>? = null) = viewModelScope.launch(Dispatchers.IO) {
        dao.insert(note)
        tags?.let {_ ->
            // delete all existing note-tag references
            dao.getTagRefs(note.id).value?.let{
                it.forEach { ref -> dao.delete(ref) }
            }

            // insert given note-tag references
            tags.forEach {
               dao.insert(it)
               dao.insert(TagNoteCrossRef(tagId = it.id, noteId = note.id))
            }
        }
    }

    class ViewModelFactory(private val mApplication: Application, private val mParam: Folder?) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NoteViewModel(mApplication, mParam) as T
        }
    }
}