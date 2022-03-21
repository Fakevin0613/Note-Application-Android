package com.noteapplication.cs398

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.noteapplication.cs398.database.Folder
import com.noteapplication.cs398.database.Note
import com.noteapplication.cs398.database.Tag
import com.noteapplication.cs398.databinding.ActivityAddNoteBinding
import java.util.*
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//class MainActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        val button: Button = findViewById<View>(R.id.button) as Button
//        button.setOnClickListener(View.OnClickListener {
//            val timePicker: DialogFragment = TimePickerFragment()
//            timePicker.show(supportFragmentManager, "time picker")
//        })
//    }
//
//    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
//        val textView: TextView = findViewById<View>(R.id.textView) as TextView
//        textView.setText("Hour: $hourOfDay Minute: $minute")
//    }
//}
class AddNoteActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var tagViewModel: TagViewModel

    private var title: String = ""
    private var content: String = ""
    private var todo: Boolean = false

    private var oldNote: Note? = null

    private var folder: Folder? = null
    private var calendar: Calendar = Calendar.getInstance()
    private var recorded_year = calendar.get(Calendar.YEAR)
    private var recorded_month = calendar.get(Calendar.MONTH)
    private var recorded_day = calendar.get(Calendar.DAY_OF_MONTH)
    private var recorded_hour = calendar.get(Calendar.HOUR_OF_DAY)
    private var recorded_minute = calendar.get(Calendar.MINUTE)
    private var REQUEST_CODE_STORAGE_PERMISSION = 1
    private var REQUEST_CODE_SELECT_IMAGE = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initialize noteViewModels
        noteViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]
        tagViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[TagViewModel::class.java]

        // set bindings
        binding = ActivityAddNoteBinding.inflate(layoutInflater)

        oldNote = intent.getSerializableExtra("note") as Note?
        oldNote?.let {
            binding.titleInput.setText(it.title)
            binding.contentInput.setText(it.content)
            binding.idRmdSwitch.isChecked = it.notify

            tagViewModel.setCurrentSelectedTags(it.id)
        }
        folder = intent.getSerializableExtra("folder") as Folder?

        // tag list configuration
        val tagList = binding.tagList.root
        tagList.adapter = TagListAdapter(tagViewModel, this)
        tagList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            private val space = 8
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(space, space, space, space)
            }
        })

        // remove tools until it is implemented
        binding.textTools.isGone = true

        // on '+' button for Tag clicked
        binding.newTagBtn.setOnClickListener {
            val name = binding.newTagInput.text.toString()
            if (name.isNotEmpty()) tagViewModel.insertTag(Tag(name))
            binding.newTagInput.setText("")
        }

        // on save button clicked
        binding.saveButton.setOnClickListener {
            Toast.makeText(this, "$title Added", Toast.LENGTH_LONG).show()

            val newNote: Note
            calendar.set(recorded_year, recorded_month, recorded_day, recorded_hour, recorded_minute)
            if (oldNote != null) {
                newNote = oldNote!!.copy(
                    title = binding.titleInput.text.toString(),
                    content = binding.contentInput.text.toString(),
                    notify = binding.idRmdSwitch.isChecked,
                    notifyAt = calendar.time.time,
                    updatedAt = Date().time
                )
                noteViewModel.updateNote(newNote, tagViewModel.getSelectedTags())
            } else {
                newNote = Note(
                    binding.titleInput.text.toString(),
                    binding.contentInput.text.toString(),
                    binding.idRmdSwitch.isChecked,
                    notifyAt = calendar.time.time,
                    folderId = folder?.id // the note does not goes to any folder for now
                )
                noteViewModel.insertNote(newNote, tagViewModel.getSelectedTags())
            }

            val data = Intent()
            data.putExtra("note", newNote)
            setResult(RESULT_OK, data)
            this.finish()
        }

        // on cancel button clicked
        binding.cancelButton.setOnClickListener { this.finish() }

        binding.addImage.setOnClickListener {
            @Override
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf("android.permission.READ_EXTERNAL_STORAGE"),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            } else {
                selectImage()
            }
        }
        if ((oldNote != null) && (oldNote!!.notify)) {
            val date:Date = Date(oldNote!!.notifyAt)
            calendar.time = date
            recorded_year = calendar.get(Calendar.YEAR)
            recorded_month = calendar.get(Calendar.MONTH)
            recorded_day = calendar.get(Calendar.DAY_OF_MONTH)
            recorded_hour = calendar.get(Calendar.HOUR_OF_DAY)
            recorded_minute = calendar.get(Calendar.MINUTE)
        }
        // time setter
        val current = LocalDateTime.now()
        var formatted = current.format(DateTimeFormatter.BASIC_ISO_DATE)
        val date: String = formatted.substring(0, 4) + "/" + formatted.substring(4, 6) + "/" + formatted.substring(6, 8)
        binding.dateInput.text = date
        formatted = current.format(DateTimeFormatter.ISO_LOCAL_TIME)
        val time: String =
            "" + (if (recorded_hour % 12 < 10) "0" else "") + recorded_hour % 12 + ":" + (if (recorded_minute < 10) "0" else "") + recorded_minute + " " + if (recorded_hour / 12 > 0) "PM" else "AM"
        binding.timeInput.text = time
        println("year: $recorded_year, month: $recorded_month, day: $recorded_day, hour:$recorded_hour, minute:$recorded_minute")
        binding.dateInput.setOnClickListener() {
            println("year: $recorded_year, month: $recorded_month, day: $recorded_day, hour:$recorded_hour, minute:$recorded_minute")
            DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, this, recorded_year, recorded_month, recorded_day).show()
        }
        binding.timeInput.setOnClickListener {
            println("year: $recorded_year, month: $recorded_month, day: $recorded_day, hour:$recorded_hour, minute:$recorded_minute")
            TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, this, recorded_hour, recorded_minute, false).show()
        }
        setContentView(binding.root)
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        var month = month + 1
        recorded_year = year
        recorded_month = month
        recorded_day = dayOfMonth
        binding.dateInput.text =
            "" + year + "/" + (if (month < 10) "0" else "") + month + "/" + (if (dayOfMonth < 10) "0" else "") + dayOfMonth
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        var hourOfDay = hourOfDay
        recorded_hour = hourOfDay
        recorded_minute = minute
        binding.timeInput.text =
            "" + (if (hourOfDay % 12 < 10) "0" else "") + hourOfDay % 12 + ":" + (if (minute < 10) "0" else "") + minute + " " + if (hourOfDay / 12 > 0) "PM" else "AM"
    }

    @Override
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }


    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == REQUEST_CODE_SELECT_IMAGE && result.resultCode == RESULT_OK) {
        val data: Intent? = result.data
        if (data != null) {
            val imageUri: Uri? = data.getData()
            if (imageUri != null) {
                try {
                    val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
                    val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
                    binding.image.setImageBitmap(bitmap)
                    //imageNote.visibility = View.VISIBLE
                    Toast.makeText(this, "image added", Toast.LENGTH_SHORT).show()

                } catch (exception: Exception) {
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "error2", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "error1", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            resultLauncher.launch(intent)
        } catch (exception: Exception) {
            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
        }
    }
}