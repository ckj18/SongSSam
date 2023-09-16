package com.example.songssam.Activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.`interface`.SongsClick
import com.example.songssam.R
import com.example.songssam.adapter.SongsAdapter
import com.example.songssam.data.Songs
import com.example.songssam.databinding.FragmentHomeBinding
import com.skydoves.balloon.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), SongsClick, Toolbar.OnMenuItemClickListener,
    androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {
    lateinit var binding:FragmentHomeBinding
    private var songList = ArrayList<Songs>()
    private lateinit var songadApter: SongsAdapter

    private val mainActivity: MainActivity by lazy {
        context as MainActivity
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeBinding.inflate(inflater)
        initSongsRecyclerView()
        // Inflate the layout for this fragment
        initPowerMenu()
        initPowerMenuBackground()
        return binding.root
    }

    private fun initSongsRecyclerView() {
        Log.d("home",songList.size.toString())
        if(songList.size==0){
            Log.d("home","balloon if 들어옴")
            binding.text.visibility=VISIBLE
            val balloon = Balloon.Builder(mainActivity)
                .setWidthRatio(1.0f)
                .setHeight(BalloonSizeSpec.WRAP)
                .setText("여기를 클릭해 AI에게 목소리를 학습시켜 주세요!")
                .setTextColorResource(R.color.black)
                .setTextSize(15f)
                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                .setArrowSize(10)
                .setArrowPosition(0.5f)
                .setPadding(12)
                .setCornerRadius(8f)
                .setBackgroundColorResource(R.color.splash_blue)
                .setBalloonAnimation(BalloonAnimation.ELASTIC)
                .setLifecycleOwner(mainActivity)
                .build()
        }
        else{
            binding.text.isVisible=false
            binding.songsRv.layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
            songadApter = SongsAdapter(songList, this)
            binding.songsRv.adapter = songadApter
        }
    }


    private fun initPowerMenuBackground() {
        binding.powerMenuBackground.setOnClickListener { }
    }

    private fun initPowerMenu() {
        binding.powerMenu.isClickable = false
        binding.powerMenu.setOnClickListener { }
        initCloseButton()

//        initReadMoreButton()
    }

//    private fun initReadMoreButton() {
//        binding.powerReadMore.setOnClickListener {
//            val intent = Intent(mainActivity, ArticleActivity::class.java)
//            intent.putExtra("address", Address)
//            startActivity(intent)
//        }
//    }

    private fun initCloseButton() {
        binding.powerClose.setOnClickListener {
            binding.powerMenu.isVisible = false
            binding.powerMenu.isClickable = false
            binding.powerMenuBackground.isClickable = false
            binding.powerMenuBackground.isVisible = false
        }
    }

    override fun SongsClick(
        title: String,
        image: String
    ) {
        Thread(kotlinx.coroutines.Runnable {
            mainActivity.runOnUiThread {
                binding.powerTitle.text = title
                Glide.with(binding.root).load(image).into(binding.powerImage)
                binding.powerMenu.isVisible = true
                binding.powerMenu.isClickable = true
                binding.powerMenuBackground.isClickable = true
                binding.powerMenuBackground.isVisible = true
            }
        }).start()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.add -> {
                startActivity(Intent(mainActivity,ChooseSongActivity::class.java))
                return true
            }
        }
        return false
    }
}