package com.noteapplication.cs398

import android.Manifest
import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.*
import android.text.Selection.setSelection
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.noteapplication.cs398.database.Folder
import com.noteapplication.cs398.database.Note
import com.noteapplication.cs398.database.Tag
import com.noteapplication.cs398.databinding.ActivityAddNoteBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class AddNoteActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var tagViewModel: TagViewModel
    lateinit var selectedColor: ColorObject

    var title: String = ""
    var content: String = ""
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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
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
            var htmlcontent = Html.fromHtml(it.content, Html.FROM_HTML_MODE_LEGACY, imgGetter, null)
            binding.contentInput.setText(htmlcontent)
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
            val spannedText: Spanned = SpannableString( binding.contentInput.text)
            var html = Html.toHtml(spannedText, Html.FROM_HTML_MODE_LEGACY)
            calendar.set(recorded_year, recorded_month, recorded_day, recorded_hour, recorded_minute)
            title = binding.titleInput.text.toString()
            content = binding.contentInput.text.toString()
            println("title: $title")
            println("content: $content")
            if (oldNote != null) {
                newNote = oldNote!!.copy(
                    title = binding.titleInput.text.toString(),
                    content = html,
                    notify = binding.idRmdSwitch.isChecked,
                    notifyAt = calendar.time.time,
                    updatedAt = Date().time
                )
                if (((calendar.time != Date(oldNote!!.notifyAt)) && binding.idRmdSwitch.isChecked)||
                    (oldNote!!.title != title) || (oldNote!!.content != content)) {
                    cancelAlarm()
                    startAlarm(calendar)
                } else if (oldNote!!.notify && !binding.idRmdSwitch.isChecked){
                    cancelAlarm()
                }
                noteViewModel.updateNote(newNote, tagViewModel.getSelectedTags())
            } else {
                newNote = Note(
                    title = binding.titleInput.text.toString(),
                    content = html,
                    notify = binding.idRmdSwitch.isChecked,
                    notifyAt = calendar.time.time,
                    createdAt = calendar.time.time,
                    folderId = folder?.id // the note does not goes to any folder for now
                )
                if (binding.idRmdSwitch.isChecked) {
                    startAlarm(calendar)
                }
                noteViewModel.insertNote(newNote, tagViewModel.getSelectedTags())
            }

            val data = Intent()
            data.putExtra("note", newNote)
            setResult(RESULT_OK, data)
            this.finish()
        }
        configureRichText()
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
        calendar.set(Calendar.SECOND, 0)
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

        loadColorSpinner()


    }

    private fun loadColorSpinner()
    {
        selectedColor = ColorList().defaultColor
        binding.highlightText.apply {
            adapter = ColorAdapter(applicationContext, ColorList().Color())
            setSelection(ColorList().Position(selectedColor), false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener
            {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long)
                {
                    selectedColor = ColorList().Color()[position]
                    var start: Int = binding.contentInput.selectionStart
                    var end: Int = binding.contentInput.selectionEnd
                    var sb = SpannableStringBuilder(binding.contentInput.text)

                    var spansback = sb.getSpans(start, end, ForegroundColorSpan::class.java)
                    for (foregroundColorSpan in spansback) sb.removeSpan(foregroundColorSpan)
                    sb.setSpan(ForegroundColorSpan(Color.parseColor(selectedColor.colorHash)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    binding.contentInput.text = sb
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
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
                selectImage()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap): String {
        val cw = ContextWrapper(applicationContext)
        val directory = cw.getDir("imageDir", MODE_PRIVATE)
        // Create imageDir
        val myPath = File(directory, bitmapImage.toString() + "image.jpeg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(myPath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return myPath.absolutePath
    }


    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == REQUEST_CODE_SELECT_IMAGE && result.resultCode == RESULT_OK) {
        val data: Intent? = result.data
        if(data != null){
            val imageUri: Uri? = data.data
            if(imageUri != null){
                try{
                    var inputStream: InputStream? = contentResolver.openInputStream(imageUri)
                    var bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
                    var path: String = bitmap?.let { saveToInternalStorage(it) }.toString()

                    var previousString : String = Html.toHtml(binding.contentInput.text, Html.FROM_HTML_MODE_LEGACY)
                    val builder = StringBuilder()
                    builder.append(previousString)
                    builder.append("<p>\n" +
                            "<a href=\"" + path + "\" ><img src=\"" + path + "\"></a>\n" +
                            "</p>")
                    var spannableString = SpannableStringBuilder()
                    spannableString.append(Html.fromHtml(builder.toString(), Html.FROM_HTML_MODE_LEGACY, imgGetter, null))
                    binding.contentInput.text = spannableString

                }catch(exception: Exception ){
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                }
            } else Toast.makeText(this, "error2", Toast.LENGTH_SHORT).show()
        } else Toast.makeText(this, "Nothing Added", Toast.LENGTH_SHORT).show()
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            resultLauncher.launch(intent)
        } catch (exception: Exception) {
            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun configureRichText(){


        binding.boldText.setOnClickListener{
            var start: Int = binding.contentInput.selectionStart
            var end: Int = binding.contentInput.selectionEnd

            var sb = SpannableStringBuilder(binding.contentInput.text)

            sb.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.contentInput.text = sb
        }

        binding.italicText.setOnClickListener{
            var start: Int = binding.contentInput.selectionStart
            var end: Int = binding.contentInput.selectionEnd

            var sb = SpannableStringBuilder(binding.contentInput.text)
            sb.setSpan(StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.contentInput.text = sb
        }

        binding.underlineText.setOnClickListener{
            var start: Int = binding.contentInput.selectionStart
            var end: Int = binding.contentInput.selectionEnd

            var sb = SpannableStringBuilder(binding.contentInput.text)
            sb.setSpan(UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.contentInput.text = sb
        }


        binding.resetText.setOnClickListener {
            var start: Int = binding.contentInput.selectionStart
            var end: Int = binding.contentInput.selectionEnd
            var sb = SpannableStringBuilder(binding.contentInput.text)
            sb.setSpan(StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            var spans = sb.getSpans(start, end, StyleSpan::class.java)
            for (styleSpan in spans) sb.removeSpan(styleSpan)

            var spansunderline = sb.getSpans(start, end, UnderlineSpan::class.java)
            for (underLineSpan in spansunderline) sb.removeSpan(underLineSpan)

            var spansback = sb.getSpans(start, end, ForegroundColorSpan::class.java)
            for (foregroundColorSpan in spansback) sb.removeSpan(foregroundColorSpan)
            binding.contentInput.text = sb
        }
    }

    private val imgGetter: Html.ImageGetter = Html.ImageGetter { source ->
        val drawable: Drawable? = Drawable.createFromPath(source)
        try {
            drawable?.setBounds(0, 0, drawable.intrinsicWidth * 4, drawable.intrinsicHeight * 4)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@ImageGetter drawable
    }

    private fun startAlarm(c: Calendar) {
        var id:Int = calendar.time.time.toInt()
        if (oldNote != null) {
            id = oldNote!!.createdAt.toInt()
        }
        val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlertReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("content", content)
        intent.putExtra("id", id)
//        Toast.makeText(this, "title: $title, content:$content", Toast.LENGTH_SHORT).show()
        val pendingIntent =
            PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        if (c.before(Calendar.getInstance())) {
            return
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
    }

    private fun cancelAlarm() {
        val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//        Toast.makeText(this, "delted: $title", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, AlertReceiver::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(this, oldNote!!.createdAt.toInt(), intent, PendingIntent.FLAG_MUTABLE)
        alarmManager.cancel(pendingIntent)
//        mTextView.setText("Alarm canceled")
    }
}
