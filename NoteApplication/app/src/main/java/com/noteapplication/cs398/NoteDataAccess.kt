package com.noteapplication.cs398

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDataAccess {
    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Note::class)
    suspend fun insert(note:Note)

    @Update(entity = Note::class)
    suspend fun update(note:Note)

    @Delete(entity = Note::class)
    suspend fun delete(note: Note)

    // *** this is the point of modification for filter and ordering feature
    @Query(
        "Select * from `Note` order by id ASC"
    )
    fun getNotes(): LiveData<List<Note>>

    @Query(
        "Select * from `Note` where id = :id"
    )
    fun getNoteById(id:Int): LiveData<List<Note>>

    // *** this is the point of modification for filter and ordering feature
    @Query(
        "Select * from `Tag` where name = :name"
    )
    fun getTag(name: String): LiveData<List<Tag>>

    // *** this is the point of modification for filter and ordering feature
    @Query(
        "Select Tag.name from `Tag`, `TagNoteCrossRef` as `Ref` where :note.id order by name ASC"
    )
    fun getTags(note: Note): LiveData<List<Tag>>
}