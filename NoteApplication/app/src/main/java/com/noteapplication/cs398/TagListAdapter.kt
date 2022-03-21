package com.noteapplication.cs398

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.noteapplication.cs398.database.Tag
import com.noteapplication.cs398.databinding.TagItemBinding

class TagListAdapter(
    private val viewModel: TagViewModel,
    private val activity: AppCompatActivity,
    private val isDisabled: Boolean = false,
    private val onItemClick: (()->Unit)? = null
) : RecyclerView.Adapter<TagListAdapter.ViewHolder>() {

    private var allTag: ArrayList<Tag> = ArrayList()

    init {
        // *** need to optimize note updates because
        // *** copying entire database for a single changed note is
        // *** unacceptable!
        // maybe keep reference from viewModel.allNotes as the source
        // and call notifyItem*(int) for every add/edit/update calls on it

        viewModel.allTags.observe(activity){
            allTag.clear()
            allTag.addAll(it)
            notifyDataSetChanged()
        }
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(val binding: TagItemBinding) : RecyclerView.ViewHolder(binding.root){
        var isSelected = false
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TagListAdapter.ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = TagItemBinding.inflate(LayoutInflater.from(viewGroup.context))

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val tagId = allTag[position].id
        val tagName = allTag[position].name

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.binding.itemTitle.text = tagName

        if(viewModel.selectedTagIds.contains(tagId))
            viewHolder.binding.root.setCardBackgroundColor(Color.CYAN)
        else
            viewHolder.binding.root.setCardBackgroundColor(Color.WHITE)

        // toggle tag selected
        if(!isDisabled) {
            viewHolder.binding.noteItem.setOnClickListener { _ ->
                println(viewModel.selectedTagIds)
                if (viewModel.selectedTagIds.contains(tagId)) {
                    viewModel.selectedTagIds.remove(tagId)
                } else {
                    viewModel.selectedTagIds.add(tagId)
                }
                onItemClick?.invoke()
                notifyItemChanged(position)
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = allTag.size

}