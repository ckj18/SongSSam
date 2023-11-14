package com.example.songssam.Fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.songssam.API.SongSSamAPI.chartjsonItems
import com.example.songssam.API.SongSSamAPI.items
import com.example.songssam.API.SongSSamAPI.songssamAPI
import com.example.songssam.Activitys.ChooseSongActivity
import com.example.songssam.Activitys.GlobalApplication
import com.example.songssam.Activitys.MainActivity
import com.example.songssam.Activitys.RecordingActivity
import com.example.songssam.adapter.AddSongAdapter
import com.example.songssam.adapter.AddSongClick
import com.example.songssam.adapter.SongsAdapter
import com.example.songssam.adapter.itemAdapter
import com.example.songssam.data.Songs
import com.example.songssam.databinding.FragmentAiBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 */
class AIFragment : Fragment(), AddSongClick {
    lateinit var binding: FragmentAiBinding
    private lateinit var songadApter: AddSongAdapter
    private var itemList = mutableListOf<chartjsonItems>()
    private val mainActivity: MainActivity by lazy {
        context as MainActivity
    }
    private lateinit var adapter: itemAdapter
    private val REQUEST_CODE_PICK_FILE = 101


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAiBinding.inflate(inflater)
        checkChosen()
        searchSong()
        // Inflate the layout for this fragment
        return binding.root
    }


    private fun checkChosen() {
        if (GlobalApplication.prefs.getString("chooseSong", "").equals("")) {
            initBeforeChosen()
        } else {
            Log.d("ai", "else")
            binding.beforeChooseSong.visibility = GONE
            binding.afterChooseSong.visibility = VISIBLE
            chartjson()
            initRecyclerView()
        }
    }

    private fun initBeforeChosen() {
        Log.d("ai", "if")
        binding.beforeChooseSong.visibility = VISIBLE
        binding.afterChooseSong.visibility = GONE
        binding.btnChooseSong.setOnClickListener {
            val intent = Intent(getActivity(), ChooseSongActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        binding.rv.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        songadApter = AddSongAdapter(itemList, this)
        binding.rv.adapter = songadApter
    }


    private fun chartjson() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://songssam.site:8443")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .readTimeout(
                        30,
                        TimeUnit.SECONDS
                    ) // Adjust the timeout as needed
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .build()
        val apiService = retrofit.create(songssamAPI::class.java)
        val call = apiService.chartJson()
        call.enqueue(object : Callback<List<chartjsonItems>> {
            override fun onResponse(
                call: Call<List<chartjsonItems>>,
                response: Response<List<chartjsonItems>>
            ) {
                if (response.isSuccessful.not()) {
                    Toast.makeText(
                        mainActivity,
                        "서버가 닫혀있습니다!",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("ai", "연결 실패")
                    return
                }
                Log.d("ai", "로그인 연결 성공")
                try {
                    itemList = response.body()?.toMutableList() ?: mutableListOf()
                    Thread(Runnable {
                        mainActivity.runOnUiThread {
                            initRecyclerView()
                        }
                    }).start()
                } catch (e: Exception) {
                }
            }

            override fun onFailure(call: Call<List<chartjsonItems>>, t: Throwable) {
                Log.d("ai", t.stackTraceToString())
                Toast.makeText(
                    mainActivity,
                    "네트워크 오류와 같은 이유로 오류 발생!",
                    Toast.LENGTH_LONG
                ).show()
                // 네트워크 오류 등 호출 실패 시 처리
            }
        })
    }

    private fun searchSong() {
        val searchView = binding.search
        val searchAutoComplete: SearchView.SearchAutoComplete =
            searchView.findViewById(androidx.appcompat.R.id.search_src_text)

        searchAutoComplete.setTextColor(Color.BLACK)
        searchAutoComplete.setHintTextColor(Color.BLACK)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://songssam.site:8443")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(
                            OkHttpClient.Builder()
                                .readTimeout(
                                    30,
                                    TimeUnit.SECONDS
                                ) // Adjust the timeout as needed
                                .connectTimeout(30, TimeUnit.SECONDS)
                                .build()
                        )
                        .build()
                    val apiService = retrofit.create(songssamAPI::class.java)
                    val call = apiService.recordableSearch(query!!, 0)
                    call.enqueue(object : Callback<List<chartjsonItems>> {
                        override fun onResponse(
                            call: Call<List<chartjsonItems>>,
                            response: Response<List<chartjsonItems>>
                        ) {
                            if (response.isSuccessful.not()) {
                                Toast.makeText(
                                    mainActivity,
                                    "서버가 닫혀있습니다!",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.d("record", "연결 실패")
                                return
                            }
                            Log.d("record", "로그인 연결 성공")
                            try {
                                itemList = response.body()?.toMutableList() ?: mutableListOf()
                                Thread(Runnable {
                                    mainActivity.runOnUiThread {
                                        initRecyclerView()
                                    }
                                }).start()
                            } catch (e: Exception) {
                            }
                        }

                        override fun onFailure(call: Call<List<chartjsonItems>>, t: Throwable) {
                            Log.d("record", t.stackTraceToString())
                            Toast.makeText(
                                mainActivity,
                                "네트워크 오류와 같은 이유로 오류 발생!",
                                Toast.LENGTH_LONG
                            ).show()
                            // 네트워크 오류 등 호출 실패 시 처리
                        }
                    })
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun isNull(songId: Long) {
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "audio/mpeg"
        chooseFile = Intent.createChooser(chooseFile, "녹음하고 싶은 노래의 가수의 목소리와 전주가 담긴 파일을 선택해주세요")
        chooseFile.putExtra("songId", songId)
        startActivityForResult(chooseFile, REQUEST_CODE_PICK_FILE)
    }

    override fun isUpLoaded() {
        TODO("Not yet implemented")
    }

    override fun isCompleted() {
        startActivity(Intent(mainActivity, RecordingActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK) {
            val selectedFile = data?.data!!.toFile()
            val songId = data.getLongExtra("songId", 0)

            val retrofit = Retrofit.Builder()
                .baseUrl("https://songssam.site:8443")
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .readTimeout(
                            30,
                            TimeUnit.SECONDS
                        ) // Adjust the timeout as needed
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .build()
                )
                .build()
            val apiService = retrofit.create(songssamAPI::class.java)
            val call = apiService.uploadSongToRecord(songId, selectedFile)
            call.enqueue(object :
                Callback<Void> { // Use Callback<Void> as the callback type
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        mainActivity,
                        "네트워크 오류와 같은 이유로 오류 발생!",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        itemList.filter { it.songID == songId }.first().status = "UPLOADED"
                    } else {
                        Toast.makeText(
                            mainActivity,
                            "서버가 닫혀있습니다!",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("updateFavoriteSong", "연결 실패")
                    }
                }
            })
        }
    }
}