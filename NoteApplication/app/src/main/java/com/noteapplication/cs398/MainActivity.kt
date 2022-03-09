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
    private lateinit var viewModel: CourseViewModel
    private lateinit var courseList: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var adapter: CourseListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[CourseViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolBar.root)

        addButton = binding.toolBar.addNew
        courseList = binding.courseList
        courseList.layoutManager = LinearLayoutManager(this)
        adapter = CourseListAdapter(viewModel, this)

//        adapter.setClickListener(this)
        courseList.adapter = adapter
        courseList.addItemDecoration(object: RecyclerView.ItemDecoration() {

            private val verticalSpaceHeight = 24

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = verticalSpaceHeight
            }
        })

        addButton.setOnClickListener{
            if(!viewModel.isAddingFolder){
                viewModel.isAddingFolder = true
                AddCourseBottomSheet(viewModel).show(supportFragmentManager, "addCourseBottomSheet")
            }
        }



        val view = binding.root
        setContentView(view)
    }
//
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.tools, menu)
        return true
    }
}