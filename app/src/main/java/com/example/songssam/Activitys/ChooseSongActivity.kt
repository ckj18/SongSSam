package com.example.songssam.Activitys

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.songssam.R
import com.example.songssam.data.itemAdapter
import com.example.songssam.data.items
import com.example.songssam.data.SelectedItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.select.Elements


class ChooseSongActivity : AppCompatActivity() {

    private var itemList: ArrayList<items> = ArrayList()
    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.rv)
    }
    private lateinit var adapter:itemAdapter
    private val editText: EditText by lazy {
        findViewById(R.id.search_edittext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_song)
        crawlingTop100()
        searchSong()
    }

    private fun initRecyclerView(itemList: ArrayList<items>) {
        recyclerView.layoutManager = GridLayoutManager(this,3)
        CoroutineScope(Dispatchers.Main).launch {
            // recyclerview adapter 초기화
            adapter = itemAdapter(itemList)
            recyclerView.adapter = adapter
        }
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
                initRecyclerView(itemList)
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
