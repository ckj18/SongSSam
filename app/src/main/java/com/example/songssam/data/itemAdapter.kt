package com.example.songssam.data

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
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.NonDisposableHandle.parent


class itemAdapter(private val itemlist:ArrayList<items>) :
    RecyclerView.Adapter<itemAdapter.ViewHolder>() {

    private var selectedItemList: HashSet<SelectedItem> = HashSet()

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val artist: TextView
        val title: TextView
        val coverImage: CircleImageView
        val checked: ImageView
        val touch: soup.neumorphism.NeumorphCardView
        init {
            artist = itemView.findViewById(R.id.artist)
            title = itemView.findViewById(R.id.title)
            coverImage = itemView.findViewById(R.id.cover_image)
            checked = itemView.findViewById(R.id.checked)
            touch = itemView.findViewById(R.id.pressed_card)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.artist.text = itemlist.get(position).artist
        holder.title.text = itemlist.get(position).title
        Glide.with(holder.itemView).load(itemlist.get(position).coverImage).into(holder.coverImage)
        holder.touch.setOnClickListener {
            // 이미 선택한 아이템 재 선택시 제거
            if (selectedItemList.contains(SelectedItem(itemlist.get(position).songID, itemlist.get(position).title, itemlist.get(position).artist))) {
                selectedItemList.remove(SelectedItem(itemlist.get(position).songID, itemlist.get(position).title, itemlist.get(position).artist))
                holder.checked.isVisible = false
            }
            // 10개 까지 선택하도록 10개를 선택한 후 더 추가하면 Toast 띄우기
            else if (selectedItemList.size >= 10) {
                Toast.makeText(holder.itemView.context, "선호하는 곡을 10개만 선택해 주세요", Toast.LENGTH_SHORT).show()
            }
            //아이템 추가
            else {
                selectedItemList.add(SelectedItem(itemlist.get(position).songID, itemlist.get(position).title, itemlist.get(position).artist))
                holder.checked.isVisible = true
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = itemlist.size

}