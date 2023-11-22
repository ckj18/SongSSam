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

class GenerateAIAdapter(
    private var itemlist: MutableList<chartjsonItems>,
    private var generatedItemList: MutableList<chartjsonItems>,
    private var generatedItemUrlPair: MutableList<Pair<Long, String>>
) :
    RecyclerView.Adapter<GenerateAIAdapter.TaskViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.generate_cover_item, viewGroup, false)
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
        if (generatedItemList.contains(item)) {
            holder.touchImage.setImageResource(R.drawable.hear)
        }
        holder.touch.setOnClickListener {
            if (generatedItemList.contains(item)) {
                val url = generatedItemUrlPair.first { it.first == item.songID }.second
                playGeneratedUrl(url)
            } else {
                // AI cover 만들기
            }
        }
    }

    private fun playGeneratedUrl(generatedUrl: String) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    val url = "https://songssam.site:8443/song/download?url=" + generatedUrl
                    setDataSource(url)
                    setOnPreparedListener {
                        it.start()
                    }
                    setOnErrorListener { _, _, _ ->
                        false
                    }
                    prepareAsync()
                }
            } else {
                if (mediaPlayer?.isPlaying == true) {
                    stopMediaPlayer()
                } else {
                    mediaPlayer?.start()
                }
            }
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Error playing audio: ${e.message}")
        }
    }

    private fun stopMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }

    class TaskViewHolder(todoTaskView: View) : RecyclerView.ViewHolder(todoTaskView) {
        val title: TextView = todoTaskView.findViewById(R.id.title)
        val artist: TextView = todoTaskView.findViewById(R.id.artist)
        val coverImage: ImageView = todoTaskView.findViewById(R.id.cover)
        val touch: ConstraintLayout = todoTaskView.findViewById(R.id.touch)
        val touchImage: ImageView = todoTaskView.findViewById(R.id.add_button)
    }
}