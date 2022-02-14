package com.noteapplication.cs398

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.noteapplication.cs398.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)



        val addButton = binding.addNew

        addButton.setOnClickListener{
            val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
            startActivity(intent)
            this.finish()
        }



        val view = binding.root
        setContentView(view)
    }
}