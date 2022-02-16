package com.noteapplication.cs398

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import com.noteapplication.cs398.databinding.AddNoteBinding
import com.noteapplication.cs398.databinding.ReadNoteBinding

class AddNoteActivity : Activity() {
    private lateinit var binding: AddNoteBinding
    private lateinit var cancel: AppCompatButton
    private lateinit var save: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddNoteBinding.inflate(layoutInflater)
        cancel = binding.Button02
        save = binding.Button03

        setContentView(binding.root)

        save.setOnClickListener(){

        }

        cancel.setOnClickListener(){

        }
    }


}