package com.noteapplication.cs398

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val dao: NoteDataAccess

    val allNotes: LiveData<List<Note>>
    val folder: MutableLiveData<Folder?> = MutableLiveData(null)
    val tags: MutableLiveData<List<Tag>> = MutableLiveData(listOf())

    init{
        dao = NoteDatabase.getDatabase(application).getNoteDataAccess()
        allNotes = Transformations.switchMap(folder){ folderValue ->
            if(folderValue != null) {
                Transformations.switchMap(tags){ tagsValue ->
                    if (tagsValue.isNotEmpty()){
                        // get notes in the folder that is also of selected tags
                        dao.getNotesByFolderIdAndTagIds(folderValue.id, tagsValue.map { it.id })
                    }else{
                        // get notes in the folder
                        dao.getNotesByFolderId(folderValue.id)
                    }
                }
            } else {
                // get all notes
                dao.getNotes()
            }
        }

    }

    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(note)
    }

    fun updateNote(note: Note, tags: List<Tag>? = null) = viewModelScope.launch(Dispatchers.IO) {
        dao.update(note)

        // need some way to detect deleted tags
        tags?.let {_ ->
            // delete all existing note-tag references
            dao.deleteAllTags(note.id)

            // insert given note-tag references
            tags.forEach {
                dao.insert(it)
                dao.insert(TagNoteCrossRef(tagId = it.id, noteId = note.id))
            }
        }

    }

    fun insertNote(note: Note, tags: List<Tag>? = null) = viewModelScope.launch(Dispatchers.IO) {
        dao.insert(note)
        tags?.let {_ ->
            // insert given note-tag references
            tags.forEach {
               dao.insert(it)
               dao.insert(TagNoteCrossRef(tagId = it.id, noteId = note.id))
            }
        }
    }

    fun notifyChanged(note:Note)  = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(note)
    }
}