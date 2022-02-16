package com.noteapplication.cs398

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.noteapplication.cs398.databinding.ReadNoteBinding

class ReadNoteActivity : AppCompatActivity(){

    val REQUEST_EDIT = 1

    private lateinit var binding:ReadNoteBinding

    private lateinit var noteItem: LiveData<Note?>

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        binding = ReadNoteBinding.inflate(layoutInflater)

        noteItem = MutableLiveData(intent.getSerializableExtra("noteItem") as Note?)

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
            startActivityForResult(intent,REQUEST_EDIT)
        }

        setContentView(binding.root)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}