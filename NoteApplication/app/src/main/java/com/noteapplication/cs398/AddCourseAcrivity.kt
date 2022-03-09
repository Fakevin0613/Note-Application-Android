package com.noteapplication.cs398

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.noteapplication.cs398.databinding.AddCourseBinding
import com.noteapplication.cs398.databinding.AddNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class AddCourseBottomSheet(private var viewModel: CourseViewModel) : BottomSheetDialogFragment() {

    private lateinit var binding: AddCourseBinding
    private lateinit var saveBtn: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = AddCourseBinding.inflate(inflater, container, false)
        saveBtn = binding.addNewCourse
        saveBtn.setOnClickListener() {
            viewModel.insertFolder(Folder(binding.courseInput.text.toString()))
        }
        return binding.root
    }


}