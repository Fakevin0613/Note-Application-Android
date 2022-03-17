package com.noteapplication.cs398

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.noteapplication.cs398.databinding.NoteItemBinding
import java.util.*
import kotlin.collections.ArrayList

class ListAdapter(private val viewModel: NoteViewModel, private val activity: AppCompatActivity) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>(), Filterable {

    private var allNotes: ArrayList<Note> = ArrayList()
    private lateinit var allNotesFull : ArrayList<Note>
    init {
        // *** need to optimize note updates because
        // *** copying entire database for a single changed note is
        // *** unacceptable!
        // maybe keep reference from viewModel.allNotes as the source
        // and call notifyItem*(int) for every add/edit/update calls on it
        viewModel.allNotes.observe(activity){
            allNotes.clear()
            allNotes.addAll(it)
            allNotesFull = ArrayList(allNotes)
            notifyDataSetChanged()
        }
//        print("allNotes.size: ")
//        println(allNotes.size)
//        print("allNotesFull.size: ")
//        println(allNotesFull.size)
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
            viewHolder.binding.itemTitle.text = allNotes[position].title
            viewHolder.binding.itemContent.text = allNotes[position].content

            // delete button
            viewHolder.binding.deleteButton.setOnClickListener { _ ->
                viewModel.deleteNote(allNotes[position])
            }

            // read note navigation
            viewHolder.binding.noteItem.setOnClickListener{ _ ->
                val intent = Intent(activity, ReadNoteActivity::class.java)
                intent.putExtra("note", allNotes[position])
                intent.putExtra("folder", viewModel.folder.value)
                activity.startActivity(intent)
            }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = allNotes.size

    override fun getFilter(): Filter {
        return exampleFilter
    }

    fun getRecentlySorted() {
        allNotes.clear()
        allNotes.addAll(allNotesFull)
        notifyDataSetChanged()
        return
    }

    fun getDescendingSorted() {
        allNotes.sortByDescending{
            it.title
        }
        notifyDataSetChanged()
        return
    }

    fun getAscendingSorted() {
        allNotes.sortBy{
            it.title
        }
        notifyDataSetChanged()
        return
    }
    private val exampleFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            println("came to filter")
            val filteredList: MutableList<Note> = ArrayList()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(allNotesFull)
            } else {
                println("filtering")
                val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                print("filtering item: ")
                println(filterPattern)
                println(allNotesFull.size)
                for (item in allNotesFull) {
                    print("filtered title: ")
                    println(item.title)
                    if (item.title.lowercase().contains(filterPattern)) {
                        println("did get some filter")
                        filteredList.add(item)
                    }
                }
                println("finished filtering")
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            allNotes.clear()
            allNotes.addAll(results.values as List<Note>)
            notifyDataSetChanged()
        }
    }

}