package com.noteapplication.cs398.database

import android.content.Context
import androidx.room.*

@Database(entities = [Note::class, Folder::class, Tag::class, TagNoteCrossRef::class], version = 12)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun getNoteDataAccess(): NoteDataAccess

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .fallbackToDestructiveMigration() // increment version number with losing data. must be removed and replaced with proper migrations
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
