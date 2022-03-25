package com.noteapplication.cs398

import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.noteapplication.cs398.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FolderViewModel
    private lateinit var courseList: RecyclerView
    private lateinit var addButton: ImageButton
    private lateinit var adapter: FolderListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[FolderViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolBar.root)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        addButton = binding.toolBar.addNew

        courseList = binding.courseList
        courseList.layoutManager = LinearLayoutManager(this)
        adapter = FolderListAdapter(viewModel, this)

        courseList.adapter = adapter
        courseList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            private val verticalSpaceHeight = 24
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = verticalSpaceHeight
            }
        })

        addButton.setOnClickListener {
            if (!viewModel.isAddingFolder) {
                viewModel.isAddingFolder = true
                NewFolderBottomSheet(viewModel).show(supportFragmentManager, "addCourseBottomSheet")
            }
        }

        val view = binding.root
        setContentView(view)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        print("override")
        val inflater = menuInflater
        inflater.inflate(R.menu.tools, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_by_recent -> {
                adapter.getRecentlySorted()
                true
            }
            R.id.sort_by_capital -> {
                adapter.getAscendingSorted()
                true
            }
            R.id.sort_by_capital_descending -> {
                adapter.getDescendingSorted()
                true
            }
            R.id.sync_data -> {
                viewModel.syncData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}