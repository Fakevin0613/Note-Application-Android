package com.noteapplication.cs398

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.noteapplication.cs398.databinding.AddCourseBinding

class AddCourseBottomSheet(private var viewModel: CourseViewModel) : BottomSheetDialogFragment() {

    private lateinit var binding: AddCourseBinding
    private lateinit var saveBtn: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = AddCourseBinding.inflate(inflater, container, false)
        saveBtn = binding.addNewCourse
        saveBtn.setOnClickListener() {
            viewModel.insertFolder(Folder(binding.courseInput.text.toString()))
            dismiss()
        }
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.isAddingFolder = false
    }

}