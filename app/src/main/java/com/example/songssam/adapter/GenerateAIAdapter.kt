package com.example.songssam.adapter

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
    private var generatedItemList:MutableList<chartjsonItems>
) :
    RecyclerView.Adapter<GenerateAIAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.generate_cover_item, viewGroup, false)
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
        if(generatedItemList.contains(item)){
            holder.touchImage.setImageResource(R.drawable.hear)
        }
        holder.touch.setOnClickListener {
            if(generatedItemList.contains(item)){
                // AI 커버 생성 요청
            }else{
                // 듣기
            }
        }
    }

    class TaskViewHolder(todoTaskView: View) : RecyclerView.ViewHolder(todoTaskView) {
        val title: TextView = todoTaskView.findViewById(R.id.title)
        val artist: TextView = todoTaskView.findViewById(R.id.artist)
        val coverImage: ImageView = todoTaskView.findViewById(R.id.cover)
        val touch: ConstraintLayout = todoTaskView.findViewById(R.id.touch)
        val touchImage: ImageView = todoTaskView.findViewById(R.id.add_button)
    }
}