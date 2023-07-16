package com.example.songssam.Activitys

import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.DocumentsContract.Document
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Adapter
import android.widget.EditText
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.songssam.R
import com.example.songssam.data.ChooseSongGridAdapter
import com.example.songssam.data.ChooseSongGridItem
import org.jsoup.Jsoup
import org.jsoup.select.Elements


class ChooseSongActivity : AppCompatActivity() {

    private var itemList: ArrayList<ChooseSongGridItem> = ArrayList()
    private lateinit var preText: String
    private val editText: EditText by lazy {
        findViewById(R.id.search_edittext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_song)
        crawlingTop100()
        searchSong()
    }

    private fun initGridView(itemList: ArrayList<ChooseSongGridItem>) {
        val gridView: GridView = findViewById(R.id.gridView)
        val adapter = ChooseSongGridAdapter(this@ChooseSongActivity, itemList)
        gridView.adapter = adapter
    }

    private fun crawlingTop100() {
        Thread(Runnable {
            val doc = Jsoup.connect("https://www.melon.com/chart/index.htm").userAgent("Chrome").get()
            val elements: Elements = doc.select(".lst50")
            // mobile-padding 클래스의 board-list의 id를 가진 것들을 elements 객체에 저장
            /*
            크롤링 하는 법 : class 는 .(class) 로 찾고 id 는 #(id) 로 검색
             */
            for (elements in elements) {  //elements의 개수만큼 반복
                val coverImage = elements.select(".image_typeAll img").attr("src")
                val title = removeBracket(elements.select(".wrap_song_info .rank01 span a").text())
                val artist = elements.select(".wrap_song_info .rank02 span").text()
                itemList.add(
                    ChooseSongGridItem(
                        coverImage, title, artist
                    )
                )     //위에서 크롤링 한 내용들을 itemlist에 추가
                Log.i(TAG, "item추가" + title + artist + coverImage)
            }
            runOnUiThread {
                initGridView(itemList)
            }
        }).start()
    }

    private fun searchSong() {
        var itemlist: ArrayList<ChooseSongGridItem> = ArrayList()
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Thread(Runnable {
                    val doc = Jsoup.connect("https://www.shazam.com/ko/charts/top-200/world").userAgent("Chrome").get()
                    val elements: Elements = doc.select(".lst50")
                    // mobile-padding 클래스의 board-list의 id를 가진 것들을 elements 객체에 저장
                    /*
                    크롤링 하는 법 : class 는 .(class) 로 찾고 id 는 #(id) 로 검색
                     */
                    for (elements in elements) {  //elements의 개수만큼 반복
                        val coverImage = elements.select(".image_typeAll img").attr("src")
                        val title = removeBracket(elements.select(".wrap_song_info .rank01 span a").text())
                        val artist = elements.select(".wrap_song_info .rank02 span").text()
                        itemList.add(
                            ChooseSongGridItem(
                                coverImage, title, artist
                            )
                        )     //위에서 크롤링 한 내용들을 itemlist에 추가
                        Log.i(TAG, "item추가" + title + artist + coverImage)
                    }
                    runOnUiThread {
                        initGridView(itemlist)
                    }
                }).start()
            }

            override fun afterTextChanged(s: Editable?) {
                TODO("검색이 내용이 없으면 그냥 Top100 크롤링 해서 시각화")
            }
        })
    }

    private fun removeBracket(text: String): String {
        if (text.indexOf("(") !== -1) {
            return text.substring(0, text.indexOf("("))
        }
        return text
    }
}

