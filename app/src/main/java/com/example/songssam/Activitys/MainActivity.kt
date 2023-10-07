package com.example.songssam.Activitys

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.songssam.Fragment.AIFragment
import com.example.songssam.Fragment.HomeFragment
import com.example.songssam.Fragment.UserFragment
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
    }


    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        when(p0.itemId){
            R.id.home ->{
                val fragmentUser = HomeFragment()
                transaction.replace(R.id.frameLayout,fragmentUser, "user")
            }
            R.id.record ->{
                val fragmentUser = AIFragment()
                transaction.replace(R.id.frameLayout,fragmentUser, "user")
            }
            R.id.user -> {
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