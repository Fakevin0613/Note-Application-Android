package com.noteapplication.cs398

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Note",
    foreignKeys = [ForeignKey(
        entity = Folder::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("folderId"),
        onDelete = ForeignKey.SET_NULL
    )]
)
data class Note (
    @ColumnInfo(name = "title") val title:String,
    @ColumnInfo(name = "content") val content:String,
    @ColumnInfo(name = "createdTime") val createdTime:String,
    @ColumnInfo(name = "notify") val notify:Boolean,
    @ColumnInfo(name = "folderId") val folderId:Long?,
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
): Serializable

@Entity(tableName = "Folder",
    foreignKeys = [ForeignKey(
        entity = Folder::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("parent"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Folder (
    @ColumnInfo(name = "name") val name:String,
    @ColumnInfo(name = "parent") val parent:Long? = null,
    @PrimaryKey(autoGenerate = true) var id: Long = 0
): Serializable

@Entity(tableName = "Tag")
data class Tag (
    @ColumnInfo(name = "name") val name:String,
    @PrimaryKey(autoGenerate = true) var id: Long = 0
)

@Entity(tableName = "TagNoteCrossRef",
    primaryKeys = ["tagId", "noteId"],
    foreignKeys = [
        ForeignKey(
            entity = Tag::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("tagId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Note::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("noteId"),
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class TagNoteCrossRef (
    val tagId:Long,
    val noteId:Long
)