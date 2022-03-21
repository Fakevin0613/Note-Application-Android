package com.noteapplication.cs398

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.noteapplication.cs398.database.Folder
import com.noteapplication.cs398.databinding.CourseItemBinding

class FolderListAdapter(private val viewModel: FolderViewModel, private val activity: AppCompatActivity) :
    RecyclerView.Adapter<FolderListAdapter.ViewHolder>() {

    private var allFolder: ArrayList<Folder> = ArrayList()

    init {
        // *** need to optimize note updates because
        // *** copying entire database for a single changed note is
        // *** unacceptable!
        // maybe keep reference from viewModel.allNotes as the source
        // and call notifyItem*(int) for every add/edit/update calls on it

        viewModel.allFolders.observe(activity){
            allFolder.clear()
            allFolder.addAll(it)
            notifyDataSetChanged()
        }
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(val binding: CourseItemBinding) : RecyclerView.ViewHolder(binding.root)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FolderListAdapter.ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = CourseItemBinding.inflate(LayoutInflater.from(viewGroup.context))

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.binding.itemTitle.text = allFolder[position].name

        // delete button
        viewHolder.binding.deleteButton.setOnClickListener { _ ->
            viewModel.deleteFolder(allFolder[position])
        }

        // read note navigation
        viewHolder.binding.noteItem.setOnClickListener{ _ ->
            val intent = Intent(activity, FolderActivity::class.java)
            intent.putExtra("folder", allFolder[position])
            activity.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = allFolder.size

}