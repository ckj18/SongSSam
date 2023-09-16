package com.example.songssam.Activitys

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.songssam.R
import com.example.songssam.adapter.RecordingItemAdapter
import com.example.songssam.data.SelectedItem
import com.example.songssam.data.items


class ChooseSongActivity2 : AppCompatActivity(), RecordingItemAdapter.SelectionChangeListener{

    private val btn: AppCompatButton by lazy {
        findViewById(R.id.btn)
    }
    private var itemList = mutableListOf<items>()
    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.rv)
    }
    private lateinit var adapter: RecordingItemAdapter
    private val editText: EditText by lazy {
        findViewById(R.id.search_edittext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_song)
        getRecommandSongs()
        initBTN()
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


    private fun getRecommandSongs() {
        //TODO("백엔드에서 받은 추천해 줄 노래들을 받는 코드 작성")
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
        adapter = RecordingItemAdapter(itemList, this) // Register the activity as the listener
        recyclerView.adapter = adapter
    }


    override fun onSelectionChanged(selectedItems: List<SelectedItem>) {
        // Update the TextView in your activity with the current selected items count
        val textView: TextView = findViewById(R.id.textView1)
        textView.text = "녹음할 곡을 3~7곡 선택 ( ${selectedItems.size} / 3~7 )"

        // Enable or disable the button based on the number of selected items
        btn.isClickable = selectedItems.size == 10
    }
}
