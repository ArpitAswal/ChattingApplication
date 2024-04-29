package com.example.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.whatsappclone.adapter.FragmentAdapter
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Find the view pager that will allow the user to swipe between fragments
        val viewPager = findViewById<ViewPager>(R.id.viewpager)
        // Create an adapter that knows which fragment should be shown on each page
        val adapter = FragmentAdapter(supportFragmentManager)
        // Set the adapter onto the view pager
        viewPager.adapter = adapter

        val tablayout = findViewById<TabLayout>(R.id.tablayout)
        tablayout.setupWithViewPager(viewPager)
    }
}