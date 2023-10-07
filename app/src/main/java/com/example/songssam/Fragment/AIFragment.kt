package com.example.songssam.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.`interface`.SongsClick
import com.example.songssam.Activitys.ChooseSongActivity
import com.example.songssam.Activitys.GlobalApplication
import com.example.songssam.Activitys.MainActivity
import com.example.songssam.adapter.SongsAdapter
import com.example.songssam.data.Songs
import com.example.songssam.databinding.FragmentAiBinding
import com.example.songssam.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass.
 */
class AIFragment : Fragment(), SongsClick {
    lateinit var binding: FragmentAiBinding
    private var songList = ArrayList<Songs>()
    private lateinit var songadApter: SongsAdapter

    private val mainActivity: MainActivity by lazy {
        context as MainActivity
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAiBinding.inflate(inflater)
        initSongsRecyclerView()
        // Inflate the layout for this fragment
        return binding.root
    }



    private fun initSongsRecyclerView() {
        if(GlobalApplication.prefs.getString("chooseSong","").equals("")){
            Log.d("ai","if")
            binding.nsv.visibility = GONE
            binding.beforeChooseSong.visibility = VISIBLE
            binding.afterChooseSong.visibility = GONE
            binding.btnChooseSong.setOnClickListener {
                val intent = Intent(getActivity(), ChooseSongActivity::class.java)
                startActivity(intent)
            }
        } else {
            Log.d("ai","else")
            binding.nsv.visibility = GONE
            binding.beforeChooseSong.visibility = GONE
            binding.afterChooseSong.visibility = VISIBLE
            binding.songsRv.layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
            songadApter = SongsAdapter(songList, this)
            binding.songsRv.adapter = songadApter
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

}