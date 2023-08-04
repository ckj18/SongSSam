package com.example.songssam.adapter

import android.util.Log
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

class itemAdapter(private var itemlist: MutableList<items>) :
    RecyclerView.Adapter<itemAdapter.TaskViewHolder>() {
    private var selectedItemList = mutableListOf<SelectedItem>()
    private var isEnable = false

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item, viewGroup, false)
        return TaskViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = itemlist[position]

        holder.artist.text = item.artist
        holder.title.text = item.title
        Glide.with(holder.itemView).load(item.coverImage).into(holder.coverImage)

        holder.checked.isVisible = selectedItemList.contains(SelectedItem(item.songID, item.title, item.artist))

        holder.touch.setOnClickListener {
            Log.d("position","Position = "+ position)
            if (selectedItemList.contains(SelectedItem(item.songID, item.title, item.artist))) {
                selectedItemList.removeAt(position)
                holder.checked.isVisible = false
                item.selected = false
            } else if (selectedItemList.size > 10) {
                Toast.makeText(it.context, "10곡만 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                item.selected = true
                holder.checked.isVisible = true
                selectedItemList.add(SelectedItem(item.songID, item.title, item.artist))
            }
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