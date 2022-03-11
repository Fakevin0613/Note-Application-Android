package com.noteapplication.cs398

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.noteapplication.cs398.databinding.AddNoteBinding
import com.noteapplication.cs398.databinding.ReadNoteBinding
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: AddNoteBinding
    private lateinit var cancel: AppCompatButton
    private lateinit var save: AppCompatButton
    private lateinit var viewModel: NoteViewModel
    private lateinit var image: ImageButton
    private lateinit var imageNote: ImageView

    private var title: String = ""
    private var content: String = ""
    private var todo: Boolean = false
    private var isEditing: Boolean = false

    private var oldId: Long? = null
    private var oldFolderId: Long? = null

    private var folder: Folder? = null

    private var REQUEST_CODE_STORAGE_PERMISSION = 1
    private var REQUEST_CODE_SELECT_IMAGE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddNoteBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]

        cancel = binding.cancelButton
        save = binding.saveButton
        image = binding.addImage
        imageNote = binding.image


        (intent.getSerializableExtra("note") as Note?)?.let {
            isEditing = true
            binding.titleInput.setText(it.title)
            binding.contentInput.setText(it.content)
            binding.idRmdSwitch.isChecked = it.notify
            oldId = it.id
            oldFolderId = it.folderId
        }
        folder = intent.getSerializableExtra("folder") as Folder?

        save.setOnClickListener{
            Toast.makeText(this, "$title Added", Toast.LENGTH_LONG).show()

            val time= SimpleDateFormat("MMM dd - yyyy")
            val current : String= time.format(Date())

            val newNote: Note

            // ******** refine this horrifying code
            if(isEditing){
                newNote = Note(
                    binding.titleInput.text.toString(),
                    binding.contentInput.text.toString(),
                    current,
                    binding.idRmdSwitch.isChecked,
                    oldFolderId,
                    oldId!!
                )
                viewModel.updateNote(newNote)

            }else{
                 newNote = Note(
                    binding.titleInput.text.toString(),
                    binding.contentInput.text.toString(),
                    current,
                    binding.idRmdSwitch.isChecked,
                    folderId = folder?.id // the note does not goes to any folder for now
                )
                viewModel.insertNote(newNote)
            }
            // ******** refine this horrifying code

            val data = Intent()
            data.putExtra("note", newNote)
            setResult(RESULT_OK, data)
            this.finish()
        }

        cancel.setOnClickListener{ this.finish() }

        image.setOnClickListener{
            @Override
            if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf("android.permission.READ_EXTERNAL_STORAGE"),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            }
            else{
                selectImage()
            }
        }

        setContentView(binding.root)
    }

    @Override
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.isNotEmpty()){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
            else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }



    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == REQUEST_CODE_SELECT_IMAGE && result.resultCode == RESULT_OK) {
        val data: Intent? = result.data
        if(data != null){
            val imageUri: Uri? = data.getData()
            if(imageUri != null){
                try{
                    val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
                    val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
                    imageNote.setImageBitmap(bitmap)
                    //imageNote.visibility = View.VISIBLE
                    Toast.makeText(this, "image added", Toast.LENGTH_SHORT).show()

                }catch(exception: Exception ){
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "error2", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this, "error1", Toast.LENGTH_SHORT).show()
        }
//        }
//        else{
//            Toast.makeText(this, "error3", Toast.LENGTH_SHORT).show()
//        }
    }

    fun selectImage() {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            resultLauncher.launch(intent)
        } catch (exception: Exception) {
            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
        }
    }
}