package com.noteapplication.cs398

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.noteapplication.cs398.databinding.ActivityAddNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var tagViewModel: TagViewModel

    private var title: String = ""
    private var content: String = ""
    private var todo: Boolean = false
    private var isEditing: Boolean = false

    private var oldId: Long? = null
    private var oldFolderId: Long? = null

    private var folder: Folder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize noteViewModels
        noteViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]
        tagViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[TagViewModel::class.java]


        // set bindings
        binding = ActivityAddNoteBinding.inflate(layoutInflater)

        (intent.getSerializableExtra("note") as Note?)?.let {
            isEditing = true
            binding.titleInput.setText(it.title)
            binding.contentInput.setText(it.content)
            binding.idRmdSwitch.isChecked = it.notify
            oldId = it.id
            oldFolderId = it.folderId

            tagViewModel.setCurrentSelectedTags(it.id)
        }
        folder = intent.getSerializableExtra("folder") as Folder?

        // tag list configuration
        val tagList = binding.tagList.root
        tagList.adapter = TagListAdapter(tagViewModel, this)
        tagList.addItemDecoration(object: RecyclerView.ItemDecoration() {
            private val space = 8
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(space, space, space, space)
            }
        })

        // remove tools until it is implemented
        binding.textTools.isGone = true

        // on '+' button for Tag clicked
        binding.newTagBtn.setOnClickListener{
            val name = binding.newTagInput.text.toString()
            if(name.isNotEmpty()) tagViewModel.insertTag(Tag(name))
        }

        // on save button clicked
        binding.saveButton.setOnClickListener{
            Toast.makeText(this, "$title Added", Toast.LENGTH_LONG).show()

            val newNote: Note

            if(isEditing){
                newNote = Note(
                    binding.titleInput.text.toString(),
                    binding.contentInput.text.toString(),
                    binding.idRmdSwitch.isChecked,
                    oldFolderId,
                    id = oldId!!
                )
                noteViewModel.updateNote(newNote, tagViewModel.getSelectedTags())

            }else{
                 newNote = Note(
                    binding.titleInput.text.toString(),
                    binding.contentInput.text.toString(),
                    binding.idRmdSwitch.isChecked,
                    folderId = folder?.id // the note does not goes to any folder for now
                )
                noteViewModel.insertNote(newNote, tagViewModel.getSelectedTags())
            }

            val data = Intent()
            data.putExtra("note", newNote)
            setResult(RESULT_OK, data)
            this.finish()
        }

        // on cancel button clicked
        binding.cancelButton.setOnClickListener{ this.finish() }

        setContentView(binding.root)
    }
}