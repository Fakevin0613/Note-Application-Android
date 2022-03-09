package com.noteapplication.cs398

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.noteapplication.cs398.databinding.OpenCourseBinding


class OpenCourseActivity : AppCompatActivity() {

    private lateinit var binding: OpenCourseBinding
    private lateinit var viewModel: NoteViewModel
    private lateinit var noteList: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var adapter: ListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = OpenCourseBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolBar.root)
        val folderItem = intent.getSerializableExtra("folder") as Folder?

        binding.title.text = folderItem?.name ?: "Notes"

        viewModel = ViewModelProvider(
            this,
            NoteViewModel.ViewModelFactory(this.application, folderItem)
        )[NoteViewModel::class.java]
//
//        viewModel = ViewModelProvider(
//            this,
//            NoteViewModel.ViewModelFactory.
////            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
//        )[NoteViewModel::class.java]


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
            val intent = Intent(this@OpenCourseActivity, AddNoteActivity::class.java)
            startActivity(intent)
        }



        val view = binding.root
        setContentView(view)
    }
    //
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        print("override")
        val inflater = menuInflater
        inflater.inflate(R.menu.tools_for_notes, menu)
        return true
    }
}