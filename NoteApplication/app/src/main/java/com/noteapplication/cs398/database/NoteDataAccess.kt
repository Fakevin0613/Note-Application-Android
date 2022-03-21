package com.noteapplication.cs398.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.text.SimpleDateFormat
import java.util.*

@Dao
interface NoteDataAccess {
    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Note::class)
    suspend fun insert(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Folder::class)
    suspend fun insert(folder: Folder): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Tag::class)
    suspend fun insert(tag: Tag): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = TagNoteCrossRef::class)
    suspend fun insert(tagNoteCrossRef: TagNoteCrossRef)

    @Update(entity = Note::class)
    suspend fun update(note: Note)

    @Update(entity = Folder::class)
    suspend fun update(folder: Folder)

    @Delete(entity = Note::class)
    suspend fun delete(note: Note)

    @Delete(entity = Folder::class)
    suspend fun delete(folder: Folder)

    @Delete(entity = Tag::class)
    suspend fun delete(tag: Tag)

    @Delete(entity = TagNoteCrossRef::class)
    suspend fun delete(tagNoteCrossRef: TagNoteCrossRef)



    // *** this is the point of modification for filter and ordering feature
    @Query(
        "Select * from `Note` order by id ASC"
    )
    fun getNotes(): LiveData<List<Note>>

    @Query(
        "Select * from `Tag` order by name ASC"
    )
    fun getTags(): LiveData<List<Tag>>

    // *** this is the point of modification for filter and ordering feature
    @Query(
        "Select Note.* from `Note`, `TagNoteCrossRef` as `Ref` " +
                "where Ref.tagId = :tagId " +
                "and Ref.noteId = Note.id " +
                "order by id ASC"
    )
    fun getNotesByTagId(tagId: Long): LiveData<List<Note>>

    // *** this is the point of modification for filter and ordering feature
    @Query(
        "Select * from `Note` where folderId = :folderId order by id ASC"
    )
    fun getNotesByFolderId(folderId: Long): LiveData<List<Note>>

    @Query(
        "Select distinct Note.* from `Note`, `TagNoteCrossRef` as `Ref` " +
                "where folderId = :folderId " +
                "and Ref.noteId = Note.id " +
                "and Ref.tagId in (:tagIds) " +
                "order by id ASC"
    )
    fun getNotesByFolderIdAndTagIds(folderId: Long, tagIds: List<Long>): LiveData<List<Note>>

    @Query(
        "Select * from `Note` where id = :id"
    )
    fun getNoteById(id:Long): LiveData<List<Note>>

    // *** this is the point of modification for filter and ordering feature
    @Query(
        "Select Tag.* from `Tag`, `TagNoteCrossRef` as `Ref` " +
                "where Ref.noteId = :noteId " +
                "and Tag.id = Ref.tagId " +
                "order by name ASC"
    )
    suspend fun getTags(noteId: Long): List<Tag>

    // *** this is the point of modification for filter and ordering feature
    @Query(
        "Select * from `TagNoteCrossRef` where noteId = :noteId"
    )
    fun getTagRefs(noteId: Long): LiveData<List<TagNoteCrossRef>>

    // *** this is the point of modification for filter and ordering feature
    @Query(
        "Select * from `Folder` order by name ASC"
    )
    fun getFolders(): LiveData<List<Folder>>

    // *** this is the point of modification for filter and ordering feature
    @Query(
        "Select * from `Folder` where id = :folderId order by name ASC"
    )
    fun getFolders(folderId: Long): LiveData<List<Folder>>

    @Query(
        "delete from TagNoteCrossRef where noteId = :noteId"
    )
    suspend fun deleteAllTags(noteId: Long)
}

fun Long.toDateString(): String{
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CANADA).format(Date(this))
}
