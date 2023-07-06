package com.example.songssam.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.songssam.R
import de.hdodenhof.circleimageview.CircleImageView


class ChooseSongGridAdapter(private var context: Context, private var itemlist: ArrayList<ChooseSongGridItem>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item: ChooseSongGridItem = itemlist[position]
        var itemView = convertView

        if (itemView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            itemView = inflater.inflate(R.layout.grid_item, parent, false)
        }

        val artist = itemView!!.findViewById<TextView>(R.id.artist)
        val title = itemView.findViewById<TextView>(R.id.title)
        val coverImage = itemView.findViewById<CircleImageView>(R.id.cover_image)

        artist.text = item.artist
        Log.i(ContentValues.TAG,"grid 화면 변경 "+artist.text)
        title.text = item.title
        Log.i(ContentValues.TAG,"grid 화면 변경 "+title.text)
        Glide.with(itemView).load(item.coverImage).into(coverImage)
        Log.i(ContentValues.TAG,"grid 화면 변경 "+coverImage.toString())

        return itemView
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return itemlist.size
    }
}