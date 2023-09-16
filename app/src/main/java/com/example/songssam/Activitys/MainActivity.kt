package com.example.songssam.Activitys

import android.content.Intent
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.songssam.Activitys.HomeFragment
import com.example.songssam.Activitys.UserFragment
import com.example.songssam.R
import com.example.songssam.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    NavigationView.OnNavigationItemSelectedListener {

    private val toolbar: androidx.appcompat.widget.Toolbar by lazy {
        findViewById(R.id.toolbar)
    }
    var fragmentHome = Fragment()

    lateinit var bottomNavigationView : BottomNavigationView
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding=ActivityMainBinding.inflate(layoutInflater)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        //첫 프래그먼트 화면은 home fragment로
        bottomNavigationView.selectedItemId = R.id.home

        initToolbar()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        val add = toolbar.findViewById<ImageView>(R.id.add)
        add.setOnClickListener {
            startActivity(Intent(this@MainActivity,ChooseSongActivity::class.java))
        }
        val balloon = Balloon.Builder(this)
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
            .setLifecycleOwner(this)
            .build()

        balloon.showAsDropDown(add)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        when(p0.itemId){
            R.id.home ->{
                toolbar.visibility=VISIBLE
                val fragmentUser = HomeFragment()
                transaction.replace(R.id.frameLayout,fragmentUser, "user")
            }
            R.id.user -> {
                toolbar.visibility=GONE
                val fragmentUser = UserFragment()
                transaction.replace(R.id.frameLayout,fragmentUser, "user")
            }
        }
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
        return true
    }
}