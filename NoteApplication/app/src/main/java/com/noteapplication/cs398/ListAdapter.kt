package com.noteapplication.cs398

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noteapplication.cs398.databinding.NoteItemBinding

class ListAdapter(private val viewModel: NoteViewModel) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var itemList: ArrayList<Note> = ArrayList()

    fun updateList(newItemList: List<Note>){
        itemList.clear()
        itemList.addAll(newItemList)
        notifyDataSetChanged()
    }
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
        viewHolder.binding.itemTitle.text = itemList[position].noteTitle
        viewHolder.binding.itemContent.text = itemList[position].noteContent
        viewHolder.binding.deleteButton.setOnClickListener {
            viewModel.deleteNote(itemList[position])
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = itemList.size

}