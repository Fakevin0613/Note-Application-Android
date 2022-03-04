package com.noteapplication.cs398

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Note")
data class Note (
    @ColumnInfo(name = "title") val noteTitle:String,
    @ColumnInfo(name = "content") val noteContent:String,
    @ColumnInfo(name = "time") val noteTime:String,
    @ColumnInfo(name = "todo") val noteTag:Boolean,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
): Serializable

@Entity(tableName = "Tag")
data class Tag (
    @ColumnInfo(name = "name") val name:String,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
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
    val tagId:String,
    val noteId:String
)