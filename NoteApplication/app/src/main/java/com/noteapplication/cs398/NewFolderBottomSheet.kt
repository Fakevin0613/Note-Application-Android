package com.noteapplication.cs398

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.noteapplication.cs398.databinding.BottomSheetNewFolderBinding

class NewFolderBottomSheet(private var viewModel: FolderViewModel) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetNewFolderBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = BottomSheetNewFolderBinding.inflate(inflater, container, false)

        binding.addNewFolder.setOnClickListener {
            viewModel.insertFolder(Folder(binding.folderInput.text.toString()))
            dismiss()
        }

        binding.folderInput.isFocusableInTouchMode = true
        binding.folderInput.requestFocus()

        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        viewModel.isAddingFolder = false
        binding.folderInput.post{
            showSoftwareKeyboard(false)
        }
        super.onDismiss(dialog)
    }

    override fun onResume() {
        super.onResume()
        binding.folderInput.post{
            showSoftwareKeyboard(true)
        }
    }

    private fun showSoftwareKeyboard(showKeyboard: Boolean) {
        val inputManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(showKeyboard){
            println(inputManager.showSoftInput(binding.folderInput,InputMethodManager.SHOW_IMPLICIT))
        }else{
            inputManager.hideSoftInputFromWindow( binding.folderInput.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY )
        }
    }
}