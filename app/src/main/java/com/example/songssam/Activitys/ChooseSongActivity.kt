package com.example.songssam.Activitys

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.songssam.R
import com.example.songssam.adapter.itemAdapter
import com.example.songssam.data.SelectedItem
import com.example.songssam.data.items
import org.jsoup.Jsoup
import org.jsoup.select.Elements


class ChooseSongActivity : AppCompatActivity(), itemAdapter.SelectionChangeListener{


    private val btn: AppCompatButton by lazy {
        findViewById(R.id.btn)
    }
    private var itemList = mutableListOf<items>()
    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.rv)
    }
    private lateinit var adapter: itemAdapter
    private val editText: EditText by lazy {
        findViewById(R.id.search_edittext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_song)
        crawlingTop100(savedInstanceState)
        initBTN()
        searchSong()

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바(App Bar)로 지정

        val actionBar: ActionBar? = supportActionBar //앱바 제어를 위해 툴바 액세스
        actionBar!!.setDisplayHomeAsUpEnabled(true) // 앱바에 뒤로가기 버튼 만들기
        actionBar?.setHomeAsUpIndicator(R.drawable.arrow_back) // 뒤로가기 버튼 색상 설정
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    private fun initBTN() {
        btn.setOnClickListener {
            val intent = Intent(this,RecordingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRecyclerView(itemList: MutableList<items>) {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        // recyclerview adapter 초기화
        adapter = itemAdapter(itemList, this) // Register the activity as the listener
        recyclerView.adapter = adapter
    }


    private fun crawlingTop100(savedInstanceState: Bundle?) {
        Thread(Runnable {
            val doc =
                Jsoup.connect("https://www.melon.com/chart/index.htm").userAgent("Chrome").get()
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
                        songID, coverImage, title, artist,false
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
                        songID, coverImage, title, artist,false
                    )
                )     //위에서 크롤링 한 내용들을 itemlist에 추가
            }
            runOnUiThread {
                initRecyclerView(itemList)
            }
        }).start()
    }

    override fun onSelectionChanged(selectedItems: List<SelectedItem>) {
        // Update the TextView in your activity with the current selected items count
        val textView: TextView = findViewById(R.id.textView1)
        textView.text = "선호하는 곡 10곡 선택  ( ${selectedItems.size} / 10 )"

        // Enable or disable the button based on the number of selected items
        btn.isClickable = selectedItems.size == 10
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

    private fun dynamicClawling(text: String) {
        //TODO 동적 크롤링을 통해 검색 글자를 통해 관련 노래 제목, 가수, 커버 이미지를 크롤링
    }

    private fun removeBracket(text: String): String {
        if (text.indexOf("(") !== -1) {
            return text.substring(0, text.indexOf("("))
        }
        return text
    }
}
