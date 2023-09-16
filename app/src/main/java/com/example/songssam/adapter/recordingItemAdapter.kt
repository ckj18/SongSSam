package com.example.songssam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.songssam.R
import com.example.songssam.data.SelectedItem
import com.example.songssam.data.items

class RecordingItemAdapter(private var itemlist: MutableList<items>, private val selectionChangeListener: RecordingItemAdapter.SelectionChangeListener) :
    RecyclerView.Adapter<RecordingItemAdapter.TaskViewHolder>() {
    private var selectedItemList = mutableListOf<SelectedItem>()

    interface SelectionChangeListener {
        fun onSelectionChanged(selectedItems: List<SelectedItem>)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecordingItemAdapter.TaskViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item, viewGroup, false)
        return RecordingItemAdapter.TaskViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecordingItemAdapter.TaskViewHolder, position: Int) {
        val item = itemlist[position]

        holder.artist.text = item.artist
        holder.title.text = item.title
        Glide.with(holder.itemView).load(item.coverImage).into(holder.coverImage)

        holder.checked.isVisible = selectedItemList.contains(SelectedItem(item.songID, item.title, item.artist))

        holder.touch.setOnClickListener {
            if (selectedItemList.contains(SelectedItem(item.songID, item.title, item.artist))) {
                selectedItemList.remove(SelectedItem(item.songID, item.title, item.artist))
                holder.checked.isVisible = false
                item.selected = false
            } else if (selectedItemList.size == 10) {
                Toast.makeText(it.context, "10곡만 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                item.selected = true
                holder.checked.isVisible = true
                selectedItemList.add(SelectedItem(item.songID, item.title, item.artist))
            }
            selectionChangeListener.onSelectionChanged(selectedItemList)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = itemlist.size

    class TaskViewHolder(todoTaskView: View) : RecyclerView.ViewHolder(todoTaskView) {
        val title: TextView = todoTaskView.findViewById(R.id.title)
        val artist: TextView = todoTaskView.findViewById(R.id.artist)
        val coverImage: ImageView = todoTaskView.findViewById(R.id.cover_image)
        val touch: soup.neumorphism.NeumorphCardView = todoTaskView.findViewById(R.id.touch)
        val checked: ImageView = todoTaskView.findViewById(R.id.checked)
    }
}