package com.example.songssam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.songssam.API.SongSSamAPI.chartjsonItems
import com.example.songssam.API.SongSSamAPI.items
import com.example.songssam.R
import com.example.songssam.data.SelectedItem

interface AddSongClick{
    fun isUpLoaded(songId:Long)
    fun isCompleted()
    fun isNull(songId:Long)
}

class AddSongAdapter(private var itemlist: MutableList<chartjsonItems>, private val addSongClick: AddSongClick) :
    RecyclerView.Adapter<AddSongAdapter.TaskViewHolder>() {

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.songs, viewGroup, false)
        return TaskViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = itemlist[position]

        holder.artist.text = item.artist
        holder.title.text = item.title
        Glide.with(holder.itemView).load(item.coverImage).into(holder.coverImage)
        when(item.status){
            "UPLOADED"-> {
                holder.touchImage.setImageResource(R.drawable.split)
            }
            "COMPLETE"-> {
                holder.touchImage.setImageResource(R.drawable.mic)
            }
            "NONE"->{
                holder.touchImage.setImageResource(R.drawable.note_add)
            }
        }
        holder.touch.setOnClickListener {
            when(item.status){
                "UPLOADED"-> {
                    addSongClick.isUpLoaded(item.songID)
                }
                "COMPLETE"-> {
                    addSongClick.isCompleted()
                }
                "NONE"-> {
                    addSongClick.isNull(item.songID)
                }
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = itemlist.size

    class TaskViewHolder(todoTaskView: View) : RecyclerView.ViewHolder(todoTaskView) {
        val title: TextView = todoTaskView.findViewById(R.id.title)
        val artist: TextView = todoTaskView.findViewById(R.id.artist)
        val coverImage: ImageView = todoTaskView.findViewById(R.id.cover)
        val touchImage: ImageView = todoTaskView.findViewById(R.id.add_button)
        val touch: ConstraintLayout = todoTaskView.findViewById(R.id.touch)
    }
}