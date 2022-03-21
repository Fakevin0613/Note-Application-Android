package com.noteapplication.cs398

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.noteapplication.cs398.database.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
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
    @Throws(Exception::class)
    fun noteWriteAndDelete() = runTest {
        val note = TestUtil.createNote()

        // add note
        noteDao.insert(note)
        val noteId = noteDao.insert(note)
        var theSameNote = noteDao.getNoteById(noteId).getOrAwaitValue()
        assertThat(theSameNote).isNotEmpty()

        // delete note
        noteDao.delete(theSameNote[0])
        theSameNote = noteDao.getNoteById(noteId).getOrAwaitValue()
        assertThat(theSameNote).isEmpty()
    }

    @Test
    @Throws(Exception::class)
    fun tagInsertSearchDelete() = runTest {
        val note = TestUtil.createNote()

        // add note
        val noteId = noteDao.insert(note)
        var noteArr = noteDao.getNoteById(noteId).getOrAwaitValue()
        assertThat(noteArr).isNotEmpty()

        // add tag
        val tag = TestUtil.createTag()
        val tagId = noteDao.insert(tag)

        // add tag-note reference
        val ref = TagNoteCrossRef(tagId = tagId, noteId = noteId)
        noteDao.insert(ref)

        // search note by tag
        noteArr = noteDao.getNotesByTagId(tagId = tagId).getOrAwaitValue()
        assertThat(noteArr).isNotEmpty()

        // search tag by note
        var tagArr = noteDao.getTags(noteId)
        assertThat(tagArr).isNotEmpty()

        // delete note
        noteDao.delete(noteArr[0])
        noteArr = noteDao.getNotes().getOrAwaitValue()
        assertThat(noteArr).isEmpty()

        // check if tag-note ref is gone
        val refArr = noteDao.getTagRefs(noteId).getOrAwaitValue()
        assertThat(refArr).isEmpty()

        // delete tag
        noteDao.delete(tagArr[0])
        tagArr = noteDao.getTags(noteId)
        assertThat(tagArr).isEmpty()
    }

    @Test
    @Throws(Exception::class)
    fun folderInsertSearchDelete() = runTest {

        // add folder
        val folder = TestUtil.createFolder()
        val folderId = noteDao.insert(folder)
        var folderArr = noteDao.getFolders().getOrAwaitValue()
        assertThat(folderArr).isNotEmpty()

        // add note with folder
        val note = TestUtil.createNote(folderId = folderId)
        val noteId = noteDao.insert(note)
        var noteArr = noteDao.getNoteById(noteId).getOrAwaitValue()
        assertThat(noteArr).isNotEmpty()

        // search note by tag
        noteArr = noteDao.getNotesByFolderId(folderId = folderId).getOrAwaitValue()
        assertThat(noteArr).isNotEmpty()

        // search folder from folderId
        folderArr = noteDao.getFolders(note.folderId!!).getOrAwaitValue()
        assertThat(folderArr).isNotEmpty()

        // delete folder
        noteDao.delete(folderArr[0])
        folderArr = noteDao.getFolders().getOrAwaitValue()
        assertThat(folderArr).isEmpty()

        // check if note.folderId is null
        noteArr = noteDao.getNoteById(noteId).getOrAwaitValue()
        assertThat(noteArr[0].folderId).isEqualTo(null)

        // delete tag
        noteDao.delete(noteArr[0])
        noteArr = noteDao.getNotes().getOrAwaitValue()
        assertThat(noteArr).isEmpty()
    }

    object TestUtil{

        fun createNote(
            title: String = "testTitle",
            content: String = "testBody",
            notify: Boolean = false,
            folderId: Long? = null
        ) = Note( title, content, notify, folderId )

        fun createTag(name: String = "testTag") = Tag(name)

        fun createFolder(name: String = "testFolder", parent: Long? = null) = Folder(name, parent)

    }
}