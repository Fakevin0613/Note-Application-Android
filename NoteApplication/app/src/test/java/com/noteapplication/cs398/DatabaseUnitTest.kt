package com.noteapplication.cs398

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseUnitTest {
    private lateinit var noteDao: NoteDataAccess
    private lateinit var db: NoteDatabase

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, NoteDatabase::class.java).build()
        noteDao = db.getNoteDataAccess()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val note = TestUtil.createNote()

        noteDao.insert(note)
        val byName = noteDao.getNoteById(note.id)
        assertThat(byName.value?.get(0), equalTo(note))
    }

    object TestUtil{

        fun createNote(
            title: String = "testTitle",
            content: String = "testBody",
            createdTime: String = "2000-01-01",
            notify: Boolean = false,
            folderId: Int? = null
        ) = Note( title, content, createdTime, notify, folderId )

    }
}