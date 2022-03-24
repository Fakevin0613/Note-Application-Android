package com.noteapplication.cs398

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.noteapplication.cs398.database.Folder
import com.noteapplication.cs398.databinding.ActivityFolderBinding


class FolderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFolderBinding
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var tagViewModel: TagViewModel
    private lateinit var adapter: ListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFolderBinding.inflate(layoutInflater)

        // set action bar
        setSupportActionBar(binding.toolBar.root)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        // configure viewModels
        noteViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]

        tagViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[TagViewModel::class.java]

        val folderItem = intent.getSerializableExtra("folder") as Folder?
        folderItem?.let { noteViewModel.folder.value = folderItem }

        binding.title.text = folderItem?.name ?: "Notes"
        adapter = ListAdapter(noteViewModel, this)
        binding.noteList.adapter = adapter
        binding.noteList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            private val verticalSpaceHeight = 24
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = verticalSpaceHeight
            }
        })

        binding.tagList.root.adapter = TagListAdapter(tagViewModel, this) {
            noteViewModel.tags.value = tagViewModel.getSelectedTags()
        }

        binding.tagList.root.addItemDecoration(object : RecyclerView.ItemDecoration() {
            private val space = 8
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(space, space, space, space)
            }
        })

        binding.addNew.setOnClickListener {
            val intent = Intent(this@FolderActivity, AddNoteActivity::class.java)
            intent.putExtra("folder", noteViewModel.folder.value)
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
        val searchItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.getFilter().filter(newText)
                return false
            }
        })
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
            else -> super.onOptionsItemSelected(item)
        }
    }
}