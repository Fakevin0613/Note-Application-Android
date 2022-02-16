package com.noteapplication.cs398

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.noteapplication.cs398.databinding.AddNoteBinding
import com.noteapplication.cs398.databinding.ReadNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: AddNoteBinding
    private lateinit var cancel: AppCompatButton
    private lateinit var save: AppCompatButton
    private lateinit var viewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddNoteBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]

        cancel = binding.cancelButton
        save = binding.saveButton

        save.setOnClickListener{
            val time= SimpleDateFormat("MMM dd - yyyy")
            val current : String= time.format(Date())

            val newNote = Note(
                binding.title.text.toString(),
                binding.idEdtNoteDesc.text.toString(),
                current,
                binding.idRmdSwitch.isChecked
            )
            viewModel.insertNote(newNote)

            Toast.makeText(this, "$title Added", Toast.LENGTH_LONG).show()

            val data = Intent()
            data.putExtra("note", newNote)
            setResult(RESULT_OK, data)
            this.finish()
        }

        cancel.setOnClickListener{ this.finish() }

        setContentView(binding.root)
    }



}