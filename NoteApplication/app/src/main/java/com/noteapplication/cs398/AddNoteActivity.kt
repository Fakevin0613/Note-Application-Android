package com.noteapplication.cs398

import android.app.Activity
import android.os.Bundle
import com.noteapplication.cs398.databinding.AddNoteBinding
import com.noteapplication.cs398.databinding.ReadNoteBinding

class AddNoteActivity : Activity() {
    private lateinit var binding: AddNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddNoteBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}