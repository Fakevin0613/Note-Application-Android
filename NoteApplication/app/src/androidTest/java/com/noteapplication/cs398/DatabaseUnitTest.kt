package com.noteapplication.cs398

import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class DatabaseUnitTest {

    //
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var noteDao: NoteDataAccess
    private lateinit var db: NoteDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, NoteDatabase::class.java)
            .allowMainThreadQueries()
            .build()
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
    fun writeUserAndReadInList() = runBlockingTest {
        val note = TestUtil.createNote()

        noteDao.insert(note)
//        val theSameNote = noteDao.getNoteById(note.id).getOrAwaitValue()
        val theSameNote = noteDao.getNotes().getOrAwaitValue()
        assertThat(theSameNote).contains(note)

    }

    object TestUtil{

        fun createNote(
            title: String = "testTitle",
            content: String = "testBody",
            createdTime: String = "2000-01-01",
            notify: Boolean = false,
            folderId: Int? = null,
            id: Int = 1
        ) = Note( title, content, createdTime, notify, folderId, id )

    }
}