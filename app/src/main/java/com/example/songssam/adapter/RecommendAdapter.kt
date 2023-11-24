package com.example.songssam.adapter

import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.songssam.API.SongSSamAPI.chartjsonItems
import com.example.songssam.R
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

interface RecommendClick{
    fun onClick(title: String, artist: String,cover:String, songId: Long, instUrl: String?)
}
class RecommendAdapter (
    private var itemlist: MutableList<chartjsonItems>,
    private val recommendClick: RecommendClick
    ) :
    RecyclerView.Adapter<RecommendAdapter.TaskViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.recommend, viewGroup, false)
            return TaskViewHolder(view)
        }

        override fun getItemCount(): Int {
            return itemlist.size
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val item = itemlist[position]
            holder.artist.text = item.artist
            holder.title.text = item.title
            Glide.with(holder.itemView).load(item.coverImage).into(holder.coverImage)

            holder.touch.setOnClickListener {
                recommendClick.onClick(item.title,item.artist,item.coverImage,item.songID,item.instUrl)
            }
        }

        class TaskViewHolder(todoTaskView: View) : RecyclerView.ViewHolder(todoTaskView) {
            val title: TextView = todoTaskView.findViewById(R.id.title)
            val artist: TextView = todoTaskView.findViewById(R.id.artist)
            val coverImage: ImageView = todoTaskView.findViewById(R.id.cover)
            val touch: ConstraintLayout = todoTaskView.findViewById(R.id.touch)
        }
}