package com.noteapplication.cs398

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.noteapplication.cs398.database.Note
import com.noteapplication.cs398.databinding.ActivityReadNoteBinding

class ReadNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReadNoteBinding
    private lateinit var tagViewModel: TagViewModel

    private lateinit var noteItem: MutableLiveData<Note?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadNoteBinding.inflate(layoutInflater)

        tagViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[TagViewModel::class.java]

        noteItem = MutableLiveData(intent.getSerializableExtra("note") as Note?)

        noteItem.observe(this) {
            it?.let {
                binding.noteTitle.text = it.title
                binding.noteContent.text = it.content
                binding.idRmdSwitch.isChecked = it.notify
                binding.idRmdSwitch.isClickable = false
                tagViewModel.setCurrentSelectedTags(it.id)
            }
        }

        binding.editButton.setOnClickListener {
            // make AddNoteActivity catch for the extra and use it as its base Note
            val intent = Intent(this@ReadNoteActivity, AddNoteActivity::class.java)
            intent.putExtra("note", noteItem.value)

            // launch activity for result
            editLauncher.launch(intent)
        }

        // tag list configuration
        val tagList = binding.tagList.root
        tagList.adapter = TagListAdapter(tagViewModel, this, isDisabled = true)
        tagList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            private val space = 8
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(space, space, space, space)
            }
        })

        binding.backButton.setOnClickListener { this.finish() }

        setContentView(binding.root)
    }

    private var editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.let {
                noteItem.value = it.getSerializableExtra("note") as Note?
                binding.tagList.root.adapter?.notifyDataSetChanged()
            }
        }
    }
}