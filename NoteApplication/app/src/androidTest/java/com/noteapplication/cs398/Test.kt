package com.noteapplication.cs398

import android.content.Context
import android.icu.text.SimpleDateFormat
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class Test {
    private lateinit var fakedatabase :NoteDatabase
    private lateinit var fakedao: NoteDataAccess

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        fakedatabase = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java).build()
        fakedao = fakedatabase.getNoteDataAccess()
    }

    @After
    fun closedb(){
        fakedatabase.close()
    }

    @Test
    fun addempty() = runBlocking{
        val title = ""
        val content = ""
        val time= SimpleDateFormat("MMM dd - yyyy")
        val current : String = time.format(Date())
        val note = Note(title, content,current,true)
        fakedao.insert(note)

        val getdao = fakedao.getNotes()
        assertEquals(getdao.value?.contains(note), true)
    }

}