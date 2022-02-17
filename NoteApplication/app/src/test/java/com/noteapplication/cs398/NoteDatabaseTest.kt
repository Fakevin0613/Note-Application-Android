package com.noteapplication.cs398

import android.content.Context
import android.icu.text.SimpleDateFormat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ApplicationProvider
import androidx.room.Room
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import junit.framework.TestCase
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test
import java.util.*
import org.junit.Assert.assertThat as junitAssertAssertThat


class NoteDatabaseTest : TestCase(){
    private lateinit var fakedatabase :NoteDatabase
    private lateinit var fakedao: NoteDataAccess

    @Before
    override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        fakedatabase = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java).build()
        fakedao = fakedatabase.getNoteDataAccess()
    }

    @After
    fun closedb(){
        fakedatabase.close()
    }

    @Test
    suspend fun addempty(){
        val title = ""
        val content = ""
        val time= SimpleDateFormat("MMM dd - yyyy")
        val current : String = time.format(Date())
        val note = Note(title, content,current,true)
        fakedao.insert(note)

        val getdao = fakedao.getNotes()
        assert(getdao.value?.contains(note) == true)
    }


}


