package com.noteapplication.cs398

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.noteapplication.cs398.databinding.AddCourseBinding
import com.noteapplication.cs398.databinding.AddTagBinding

class AddTagBottomSheet(private var viewModel: TagViewModel) : BottomSheetDialogFragment() {

    private lateinit var binding: AddTagBinding
    private lateinit var saveBtn: ImageButton
    private lateinit var tagList: RecyclerView
    private lateinit var adapter: TagListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = AddTagBinding.inflate(inflater, container, false)
        saveBtn = binding.addNewTag
        tagList = binding.tagList
//        tagList.layoutManager = LinearLayoutManager(this)
//        adapter = TagListAdapter(viewModel, this)
//        adapter.setClickListener(this)
//        tagList.adapter = adapter
        saveBtn.setOnClickListener() {
            print("clicked")
            viewModel.insertTag(Tag(binding.tagInput.text.toString()))
        }
        return binding.root
    }


}