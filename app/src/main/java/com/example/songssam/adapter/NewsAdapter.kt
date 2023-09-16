package com.example.songssam.adapter

import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.`interface`.SongsClick
import com.example.songssam.data.Songs


class SongsAdapter(val itemlist: ArrayList<Songs>, val listener: SongsClick) :
    RecyclerView.Adapter<SongsAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.example.songssam.R.layout.songs, parent, false)

        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val item = itemlist[position]
        holder.title.text = item.title
//        if(item.keyword!="")
//        {
//            setHighLightedText(holder.title, item.keyword)
//        }
        Glide.with(holder.itemView).load(item.cover).into(holder.cover)
        var body: String = ""
        holder.touch.setOnClickListener {
        }
    }
    fun setHighLightedText(tv: TextView, textToHighlight: String) {
        val tvt = tv.text.toString()
        var ofe = tvt.indexOf(textToHighlight, 0)
        val wordToSpan: Spannable = SpannableString(tv.text)
        var ofs = 0
        while (ofs < tvt.length && ofe != -1) {
            ofe = tvt.indexOf(textToHighlight, ofs)
            if (ofe == -1) break else {
                // set color here
                wordToSpan.setSpan(
                    BackgroundColorSpan(-0x100),
                    ofe,
                    ofe + textToHighlight.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE)
            }
            ofs = ofe + 1
        }
    }


    override fun getItemCount(): Int {
        return itemlist.size
    }

    class CustomViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val title = itemView.findViewById<TextView>(com.example.songssam.R.id.title)
        val cover = itemView.findViewById<ImageView>(com.example.songssam.R.id.cover)
        val touch =
            itemView.findViewById<ConstraintLayout>(com.example.songssam.R.id.touch)
    }
}