package com.example.whatsappclone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import com.example.whatsappclone.adapter.FragmentAdapter
import com.example.whatsappclone.firebase.References
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        References.getAllUsersChat(this@HomeActivity)
        References.getAllGroupsChat(this@HomeActivity)
        // Find the view pager that will allow the user to swipe between fragments
        viewPager = findViewById<ViewPager>(R.id.viewpager)
        // Create an adapter that knows which fragment should be shown on each page
        val adapter = FragmentAdapter(supportFragmentManager)
        // Set the adapter onto the view pager
        viewPager.adapter = adapter

        val tablayout = findViewById<TabLayout>(R.id.tablayout)
        tablayout.setupWithViewPager(viewPager)

        val filter = IntentFilter("NAVIGATE_TO_FRAGMENT")
        registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val fragmentIndex = intent?.getIntExtra("TARGET_FRAGMENT_INDEX", 0) ?: 0
            viewPager.setCurrentItem(fragmentIndex, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver
        unregisterReceiver(receiver)
    }
}