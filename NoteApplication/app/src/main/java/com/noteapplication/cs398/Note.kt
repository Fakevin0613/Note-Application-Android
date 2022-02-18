package com.noteapplication.cs398

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Table")
data class Note(
    @ColumnInfo(name = "title")val noteTitle:String,
    @ColumnInfo(name = "content")val noteContent:String,
    @ColumnInfo(name = "time")val noteTime:String,
    @ColumnInfo(name = "Todo")val noteTag:Boolean,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
): Serializable