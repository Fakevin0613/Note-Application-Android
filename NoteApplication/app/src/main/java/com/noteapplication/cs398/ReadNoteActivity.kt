package com.noteapplication.cs398

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.noteapplication.cs398.databinding.ReadNoteBinding

class ReadNoteActivity : AppCompatActivity(){

    val REQUEST_EDIT = 1

    private lateinit var binding:ReadNoteBinding

    private lateinit var noteItem: MutableLiveData<Note?>

    private var editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.let {
                noteItem.value = it.getSerializableExtra("note") as Note?
            }
        }
    }

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        binding = ReadNoteBinding.inflate(layoutInflater)

        noteItem = MutableLiveData(intent.getSerializableExtra("note") as Note?)

        noteItem.observe(this){
            it?.let {
                binding.noteTitle.text = it.noteTitle
                binding.noteContent.text = it.noteContent
                binding.idRmdSwitch.isChecked = it.noteTag
            }
        }

        binding.editButton.setOnClickListener {
            // make AddNoteActivity catch for the extra and use it as its base Note
            val intent = Intent(this@ReadNoteActivity, AddNoteActivity::class.java)
            intent.putExtra("noteItem", noteItem.value)

            // launch activity for result
            editLauncher.launch(intent)
        }

        setContentView(binding.root)
    }
}