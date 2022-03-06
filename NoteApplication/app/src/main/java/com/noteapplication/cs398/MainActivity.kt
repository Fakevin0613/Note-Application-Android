package com.noteapplication.cs398

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.noteapplication.cs398.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NoteViewModel
    private lateinit var noteList: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var adapter: ListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]

        ////// >>>>>>>>>>> testing area
//        for(i in 1..5){
//            viewModel.insertNote(Note("title", "content something something text thong hting", "time", true))
//        }
        ///// <<<<<<<<<<<<

        binding = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolBar.root)

        addButton = binding.addNew
        noteList = binding.noteList
        noteList.layoutManager = LinearLayoutManager(this)
        adapter = ListAdapter(viewModel, this)

//        adapter.setClickListener(this)
        noteList.adapter = adapter
        noteList.addItemDecoration(object: RecyclerView.ItemDecoration() {

            private val verticalSpaceHeight = 24

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = verticalSpaceHeight
            }
        })

        addButton.setOnClickListener{
            val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
            startActivity(intent)
        }



        val view = binding.root
        setContentView(view)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        print("override")
        val inflater = menuInflater
        inflater.inflate(R.menu.tools_for_notes, menu)
        return true
    }
}