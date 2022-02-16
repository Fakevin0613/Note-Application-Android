package com.noteapplication.cs398

import android.app.Activity
import android.os.Bundle
import com.noteapplication.cs398.databinding.ReadNoteBinding

class ReadNoteActivity :Activity(){
    private lateinit var binding:ReadNoteBinding

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)

        binding=ReadNoteBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}