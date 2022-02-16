package com.noteapplication.cs398

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDataAccess {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note:Note)

    @Update
    suspend fun update(note:Note)

    @Delete
    suspend fun delete(note: Note)

    // *** this is the point of modification for filter and ordering feature
    @Query(
        "Select * from `Table` order by id ASC"
    )
    fun getNotes(): LiveData<List<Note>>
}