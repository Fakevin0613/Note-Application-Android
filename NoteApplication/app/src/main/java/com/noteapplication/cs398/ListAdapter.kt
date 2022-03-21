package com.noteapplication.cs398

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.noteapplication.cs398.database.Note
import com.noteapplication.cs398.databinding.NoteItemBinding
import java.util.*

class ListAdapter(private val viewModel: NoteViewModel, private val activity: AppCompatActivity) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>(), Filterable {

    private var allNotes: ArrayList<Note> = ArrayList()
    private lateinit var allNotesFull: ArrayList<Note>

    init {
        // *** need to optimize note updates because
        // *** copying entire database for a single changed note is
        // *** unacceptable!
        // maybe keep reference from viewModel.allNotes as the source
        // and call notifyItem*(int) for every add/edit/update calls on it
        viewModel.allNotes.observe(activity) {
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
        if (allNotes[position].notify) {
            val date:Date = Date(allNotes[position].notifyAt)
            val calendarReminder = Calendar.getInstance()
            calendarReminder.time = date
            val calendar = Calendar.getInstance()
            viewHolder.binding.itemContent.setText(whatText(calendar, calendarReminder))
            viewHolder.binding.itemContent.setTextColor(whatColor(calendar, calendarReminder))
        } else {
            viewHolder.binding.itemContent.setText("")
            viewHolder.binding.itemContent.width = 0;
        }
        // delete button
        viewHolder.binding.deleteButton.setOnClickListener { _ ->
            viewModel.deleteNote(allNotes[position])
        }

        // read note navigation
        viewHolder.binding.noteItem.setOnClickListener { _ ->
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

    fun whatText(calendar: Calendar, calendarReminder: Calendar) : String {
        val currentDate:String = "" + calendarReminder.get(Calendar.YEAR) + "-" + (if (calendarReminder.get(Calendar.MONTH) < 10) "0" else "") + calendarReminder.get(Calendar.MONTH) + "-" + (if (calendarReminder.get(Calendar.DAY_OF_MONTH) < 10) "0" else "") + calendarReminder.get(Calendar.DAY_OF_MONTH)
        if (calendar.get(Calendar.YEAR) == calendarReminder.get(Calendar.YEAR) &&
            calendar.get(Calendar.MONTH) == calendarReminder.get(Calendar.MONTH)) {
            val current:String = (if (calendarReminder.get(Calendar.HOUR_OF_DAY) % 12 < 10) "0" else "") + calendarReminder.get(Calendar.HOUR_OF_DAY) % 12 + ":" + (if (calendarReminder.get(Calendar.MINUTE) < 10) "0" else "") + calendarReminder.get(Calendar.MINUTE) + " " + if (calendarReminder.get(Calendar.HOUR_OF_DAY) / 12 > 0) "PM" else "AM"
            if (calendar.get(Calendar.DAY_OF_MONTH) == calendarReminder.get(Calendar.DAY_OF_MONTH)) {
                return "Today, " + current
            } else if (calendar.get(Calendar.DAY_OF_MONTH) - 1 == calendarReminder.get(Calendar.DAY_OF_MONTH)) {
                return "Yesterday, " + current
            } else if (calendar.get(Calendar.DAY_OF_MONTH) + 1 == calendarReminder.get(Calendar.DAY_OF_MONTH)) {
                return "Tomorrow, " + current
            } else {
                return currentDate
            }
        }  else {
            return currentDate
        }
    }

    fun whatColor(calendar: Calendar, calendarReminder: Calendar) : Int {
//        println(calendar.time < calendarReminder.time)
        if (calendar.compareTo(calendarReminder) > 0) {
            return Color.RED
        }
        return Color.BLACK
    }

    fun getRecentlySorted() {
        allNotes.clear()
        allNotes.addAll(allNotesFull)
        notifyDataSetChanged()
        return
    }

    fun getDescendingSorted() {
        allNotes.sortByDescending {
            it.title
        }
        notifyDataSetChanged()
        return
    }

    fun getAscendingSorted() {
        allNotes.sortBy {
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