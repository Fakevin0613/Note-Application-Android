package com.noteapplication.cs398

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.noteapplication.cs398.databinding.ActivityAddNoteBinding

class Span {
    companion object{
        fun BoldText(binding: ActivityAddNoteBinding, context: Context){
            try{
                var start: Int = binding.contentInput.selectionStart
                var end: Int = binding.contentInput.selectionEnd

                var sb = SpannableStringBuilder(binding.contentInput.text)

                sb.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding.contentInput.text = sb
            }
            catch(e:Exception){
                Toast.makeText(context, "Please select text first", Toast.LENGTH_SHORT)
            }

        }

        fun ItalicText(binding: ActivityAddNoteBinding, context: Context){
            try{
                var start: Int = binding.contentInput.selectionStart
                var end: Int = binding.contentInput.selectionEnd

                var sb = SpannableStringBuilder(binding.contentInput.text)
                sb.setSpan(StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding.contentInput.text = sb
            }
            catch(e:Exception){
                Toast.makeText(context, "Please select text first", Toast.LENGTH_SHORT)
            }

        }

        fun UnderlingText(binding: ActivityAddNoteBinding, context: Context){
            try{
                var start: Int = binding.contentInput.selectionStart
                var end: Int = binding.contentInput.selectionEnd

                var sb = SpannableStringBuilder(binding.contentInput.text)
                sb.setSpan(UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding.contentInput.text = sb
            }
            catch(e:Exception){
                Toast.makeText(context, "Please select text first", Toast.LENGTH_SHORT)
            }
        }

        fun ResetText(binding: ActivityAddNoteBinding, context: Context){
            try{
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
//                sb.setSpan(ForegroundColorSpan(Color.parseColor("#080400")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                binding.contentInput.text = sb
            }
            catch(e:Exception){
                Toast.makeText(context, "Please select text first", Toast.LENGTH_SHORT)
            }
        }

    }

}