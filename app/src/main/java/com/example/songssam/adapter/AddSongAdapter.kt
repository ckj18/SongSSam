package com.example.songssam.adapter

import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.example.songssam.API.SongSSamAPI.songssamAPI
import com.example.songssam.R
import com.example.songssam.data.SelectedItem
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface AddSongClick{
    fun isUpLoaded(songId:Long)
    fun isCompleted()
    fun isNull(songId:Long)
    fun isProcessing()
}

class AddSongAdapter(private var itemlist: MutableList<chartjsonItems>, private val addSongClick: AddSongClick) :
    RecyclerView.Adapter<AddSongAdapter.TaskViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
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
            "PROCESS"->{
                holder.touchImage.setImageResource(R.drawable.loading)
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
                "PROCESS"->{
                    addSongClick.isProcessing()
                }
            }
        }
        holder.touch.setOnLongClickListener {
            if(item.status!="NONE"){
                Log.d("long","playOrigin")
                playOriginUrl(item.originUrl)
            }
            true
        }
        holder.touch.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP && mediaPlayer?.isPlaying == true) {
                stopMediaPlayer()
            }
            false
        }
    }

    private fun playOriginUrl(originUrl: String) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    val url = "https://songssam.site:8443/song/download?url="+originUrl
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
                    mediaPlayer?.pause()
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