package com.example.songssam.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.`interface`.SongsClick
import com.example.songssam.API.SongSSamAPI.ChartJsonItem
import com.example.songssam.API.SongSSamAPI.Voice
import com.example.songssam.API.SongSSamAPI.chartjsonItems
import com.example.songssam.API.SongSSamAPI.songssamAPI
import com.example.songssam.Activitys.MainActivity
import com.example.songssam.R
import com.example.songssam.adapter.AddSongAdapter
import com.example.songssam.adapter.GenerateAIAdapter
import com.example.songssam.adapter.SongsAdapter
import com.example.songssam.data.Songs
import com.example.songssam.databinding.FragmentHomeBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private var itemList = mutableListOf<chartjsonItems>()
    private var generatedItemList = mutableListOf<chartjsonItems>()
    private var sampleVoiceList = mutableListOf<Voice>()
    private lateinit var songAdapter: GenerateAIAdapter

    private val mainActivity: MainActivity by lazy {
        context as MainActivity
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        getSampleVoice()
        initSpinner()
        getCompletedList()
        getGeneratedSongList(sampleVoiceList.first().id)
        initRecyclerView()
        return binding.root
    }

    private fun getGeneratedSongList(ptrId : Long){
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
        val call = apiService.getGeneratedSongList(ptrId)
        call.enqueue(object : Callback<List<ChartJsonItem>> {
            override fun onResponse(
                call: Call<List<ChartJsonItem>>,
                response: Response<List<ChartJsonItem>>
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
                    response.body()?.all {
                        generatedItemList.add(it.song)
                        true
                    }
                } catch (e: Exception) {
                }
            }

            override fun onFailure(call: Call<List<ChartJsonItem>>, t: Throwable) {
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

    private fun getCompletedList() {
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
        val call = apiService.getCompletedList()
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
    private fun getSampleVoice(){
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
        val call = apiService.getSampleVoiceList()
        call.enqueue(object : Callback<List<Voice>> {
            override fun onResponse(
                call: Call<List<Voice>>,
                response: Response<List<Voice>>
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
                    sampleVoiceList = response.body()?.toMutableList() ?: mutableListOf()
                } catch (e: Exception) {
                }
            }

            override fun onFailure(call: Call<List<Voice>>, t: Throwable) {
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
    private fun initRecyclerView() {
        binding.rv.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        songAdapter = GenerateAIAdapter(itemList,generatedItemList)
        binding.rv.adapter = songAdapter
    }

    private fun initSpinner() {
        val spinner = binding.voiceSpinner
        val dataArray = mutableListOf<String>()
        sampleVoiceList.all {
            dataArray.add(it.name)
        }
        val adapter :ArrayAdapter<String> = ArrayAdapter(mainActivity,
            soup.neumorphism.R.layout.support_simple_spinner_dropdown_item,dataArray)

        spinner.adapter  = adapter
        spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                getGeneratedSongList(sampleVoiceList.find {
                    it.name == dataArray[position]
                }!!.id)
                adapter.notifyDataSetChanged()
                initRecyclerView()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
}