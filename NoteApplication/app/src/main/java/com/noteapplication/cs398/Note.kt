package com.noteapplication.cs398

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//@Serializable
@Entity(tableName = "Table")
class Note(
    @ColumnInfo(name = "title")val noteTitle:String,
    @ColumnInfo(name = "content")val noteContent:String,
    @ColumnInfo(name = "time")val noteTime:String,
    @ColumnInfo(name = "Tag")val noteTag:String
){
    @PrimaryKey(autoGenerate = true)
    var id = 0

    init {


    }
}