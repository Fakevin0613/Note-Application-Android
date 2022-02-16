package com.noteapplication.cs398

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.noteapplication.cs398.databinding.NoteItemBinding

class ListAdapter(private val viewModel: NoteViewModel, private val activity: AppCompatActivity) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(val binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = NoteItemBinding.inflate(LayoutInflater.from(viewGroup.context))

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewModel.allNotes.value?.let {
            viewHolder.binding.itemTitle.text = it[position].noteTitle
            viewHolder.binding.itemContent.text = it[position].noteContent

            // delete button
            viewHolder.binding.deleteButton.setOnClickListener { _ ->
                viewModel.deleteNote(it[position])
            }

            // read note navigation
            viewHolder.binding.noteItem.setOnClickListener{ _ ->
                val intent = Intent(activity, ReadNoteActivity::class.java)
//                intent.putExtra("noteItem", it[position].ser)
                activity.startActivity(intent)
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = viewModel.allNotes.value?.size ?: 0

}