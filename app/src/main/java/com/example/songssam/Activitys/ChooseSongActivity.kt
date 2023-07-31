package com.example.songssam.Activitys

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.isVisible
import com.example.songssam.R
import com.example.songssam.data.itemAdapter
import com.example.songssam.data.items
import com.example.songssam.data.SelectedItem
import org.jsoup.Jsoup
import org.jsoup.select.Elements


class ChooseSongActivity : AppCompatActivity() {

    private var itemList: ArrayList<items> = ArrayList()
    private var selectedItemList: HashSet<SelectedItem> = HashSet()
    private val gridView: GridView by lazy {
        findViewById(R.id.gridView)
    }
    private val editText: EditText by lazy {
        findViewById(R.id.search_edittext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_song)
        crawlingTop100()
        searchSong()
    }

    private fun setItemClickListener(itemList: ArrayList<items>){
        gridView.setOnItemClickListener { parent, view, position, id ->
            // 이미 선택한 아이템 재 선택시 제거
             if(selectedItemList.contains(SelectedItem(itemList[position].songID,itemList[position].title,itemList[position].artist))){
                selectedItemList.remove(SelectedItem(itemList[position].songID,itemList[position].title,itemList[position].artist))
                 gridView[position].findViewById<ImageView>(R.id.checked).isVisible = false
            }
            // 10개 까지 선택하도록 10개를 선택한 후 더 추가하면 Toast 띄우기
            else if(selectedItemList.size>=10){
                Toast.makeText(this,"선호하는 곡을 10개만 선택해 주세요",Toast.LENGTH_SHORT).show()
            }
            //아이템 추가
            else{
                selectedItemList.add(SelectedItem(itemList[position].songID,itemList[position].title,itemList[position].artist))
                 gridView[position].findViewById<ImageView>(R.id.checked).isVisible = true
                 Log.d("clicked","position = "+ position)
            }
        }
    }

    private fun initGridView(itemList: ArrayList<items>) {
        val adapter = itemAdapter(this@ChooseSongActivity, itemList)
        gridView.adapter = adapter
        setItemClickListener(itemList)
    }

    private fun crawlingTop100() {
        Thread(Runnable {
            val doc = Jsoup.connect("https://www.melon.com/chart/index.htm").userAgent("Chrome").get()
            var elements: Elements = doc.select(".lst50")
            // mobile-padding 클래스의 board-list의 id를 가진 것들을 elements 객체에 저장
            /*
            크롤링 하는 법 : class 는 .(class) 로 찾고 id 는 #(id) 로 검색
             */
            for (elements in elements) {  //elements의 개수만큼 반복
                val songID = elements.attr("data-song-no")
                val coverImage = elements.select(".image_typeAll img").attr("src")
                val title = removeBracket(elements.select(".wrap_song_info .rank01 span a").text())
                val artist = elements.select(".wrap_song_info .rank02 span").text()
                itemList.add(
                    items(
                        songID, coverImage, title, artist
                    )
                )     //위에서 크롤링 한 내용들을 itemlist에 추가
            }
            elements = doc.select(".lst100")
            for (elements in elements) {  //elements의 개수만큼 반복
                val songID = elements.attr("data-song-no")
                val coverImage = elements.select(".image_typeAll img").attr("src")
                val title = removeBracket(elements.select(".wrap_song_info .rank01 span a").text())
                val artist = elements.select(".wrap_song_info .rank02 span").text()
                itemList.add(
                    items(
                        songID, coverImage, title, artist
                    )
                )     //위에서 크롤링 한 내용들을 itemlist에 추가
            }
            runOnUiThread {
                initGridView(itemList)
            }
        }).start()
    }

    private fun searchSong() {
        var itemlist: ArrayList<items> = ArrayList()
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dynamicClawling(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {
                TODO("검색이 내용이 없으면 그냥 Top100 크롤링 해서 시각화")
            }
        })
    }

    private fun dynamicClawling(text : String){
        //TODO 동적 크롤링을 통해 검색 글자를 통해 관련 노래 제목, 가수, 커버 이미지를 크롤링
    }

    private fun removeBracket(text: String): String {
        if (text.indexOf("(") !== -1) {
            return text.substring(0, text.indexOf("("))
        }
        return text
    }
}
