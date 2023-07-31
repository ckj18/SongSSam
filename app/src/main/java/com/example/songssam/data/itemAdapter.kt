package com.example.songssam.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.songssam.R
import de.hdodenhof.circleimageview.CircleImageView

class ChooseSongGridAdapter(private var context: Context, private var itemlist: ArrayList<items>) : BaseAdapter() {
    private var selectedItemList: HashSet<SelectedItem> = HashSet()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item: items = itemlist[position]
        var itemView = convertView

        if (itemView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            itemView = inflater.inflate(R.layout.grid_item, parent, false)
        }

        val artist = itemView!!.findViewById<TextView>(R.id.artist)
        val title = itemView.findViewById<TextView>(R.id.title)
        val coverImage = itemView.findViewById<CircleImageView>(R.id.cover_image)
        val checked = itemView.findViewById<ImageView>(R.id.checked)

        artist.text = item.artist
        title.text = item.title
        Glide.with(itemView).load(item.coverImage).into(coverImage)

        itemView.setOnClickListener {
            // 이미 선택한 아이템 재 선택시 제거
            if(selectedItemList.contains(SelectedItem(item.songID,item.title,item.artist))){
                selectedItemList.remove(SelectedItem(item.songID,item.title,item.artist))
                checked.isVisible=false
            }
            // 10개 까지 선택하도록 10개를 선택한 후 더 추가하면 Toast 띄우기
            else if(selectedItemList.size>=10){
                Toast.makeText(context,"선호하는 곡을 10개만 선택해 주세요", Toast.LENGTH_SHORT).show()
            }
            //아이템 추가
            else{
                selectedItemList.add(SelectedItem(item.songID,item.title,item.artist))
                checked.isVisible=true
            }
        }

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