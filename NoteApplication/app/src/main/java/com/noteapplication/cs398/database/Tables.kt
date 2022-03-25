package com.noteapplication.cs398.database

import androidx.room.*
import kotlinx.serialization.json.JsonNames
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Entity(
    tableName = "Note",
//    foreignKeys = [ForeignKey(
//        entity = Folder::class,
//        parentColumns = arrayOf("id"),
//        childColumns = arrayOf("folderId"),
//        onDelete = ForeignKey.CASCADE
//    )]
)
data class Note(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "notify") val notify: Boolean,
    @ColumnInfo(name = "notifyAt") val notifyAt: Long = Date().time,
    @ColumnInfo(name = "folderId") val folderId: Long?,
    @ColumnInfo(name = "createdAt") val createdAt: Long = Date().time,
    @ColumnInfo(name = "updatedAt") val updatedAt: Long = Date().time,
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
) : Serializable

@Entity(
    tableName = "Folder",
//    foreignKeys = [ForeignKey(
//        entity = Folder::class,
//        parentColumns = arrayOf("id"),
//        childColumns = arrayOf("parent"),
//        onDelete = ForeignKey.CASCADE
//    )]
)
data class Folder(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "parent") val parent: Long? = null,
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "createdAt") val createdAt: Long = Date().time,
    @ColumnInfo(name = "updatedAt") val updatedAt: Long = Date().time,
) : Serializable

@Entity(
    tableName = "Tag",
    indices = [Index(
        value = ["name"],
        unique = true
    )]
)
data class Tag(
    @ColumnInfo(name = "name") val name: String,
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "createdAt") val createdAt: Long = Date().time,
    @ColumnInfo(name = "updatedAt") val updatedAt: Long = Date().time,
)

@Entity(
    tableName = "TagNoteCrossRef",
    primaryKeys = ["tagId", "noteId"],
//    foreignKeys = [
//        ForeignKey(
//            entity = Tag::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("tagId"),
//            onDelete = ForeignKey.CASCADE
//        ),
//        ForeignKey(
//            entity = Note::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("noteId"),
//            onDelete = ForeignKey.CASCADE
//        ),
//    ]
)
data class TagNoteCrossRef(
    val tagId: Long,
    val noteId: Long,
    @ColumnInfo(name = "createdAt") val createdAt: Long = Date().time,
    @ColumnInfo(name = "updatedAt") val updatedAt: Long = Date().time,
)

@Entity(tableName = "DeleteLog")
data class DeleteLog(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "tableName") val tableName: String,
    @ColumnInfo(name = "idPrimary") val idPrimary: Long,
    @ColumnInfo(name = "idSecondary") val idSecondary: Long? = null,
    @ColumnInfo(name = "deletedAt") val deletedAt: Long = Date().time,
)