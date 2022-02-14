package com.noteapplication.cs398

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val addButton: MaterialButton = findViewById(R.id.notefolderadd);

        addButton.setOnClickListener{
            val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
            startActivity(intent)
            this.finish()
        }




    }
}