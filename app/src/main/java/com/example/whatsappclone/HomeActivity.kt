package com.example.whatsappclone

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.viewpager.widget.ViewPager
import com.example.whatsappclone.adapter.FragmentAdapter
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.fragments.Chats
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {

    companion object {
        private var instance: HomeActivity? = null

        fun getInstance(): HomeActivity? {
            return instance
        }
    }

    private lateinit var chats: Chats
    private lateinit var viewPager: ViewPager
    private lateinit var toolbar: Toolbar

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        References.getAllUsersChat(this@HomeActivity)
        References.getAllGroupsChat(this@HomeActivity)
        // Find the view pager that will allow the user to swipe between fragments
        viewPager = findViewById(R.id.viewpager)
        // Create an adapter that knows which fragment should be shown on each page
        val adapter = FragmentAdapter(supportFragmentManager)
        // Set the adapter onto the view pager
        viewPager.adapter = adapter
        instance = this
        // Initialize the toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        defaultToolbar()

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
        instance = null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu, this will add items to the action bar if present
        menuInflater.inflate(R.menu.contact_action_btn, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                // Handle delete action
                dialogBox()
                true
            }

            R.id.action_more -> {
                // Handle more action
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun dialogBox() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialogbox)

        // Set the dialog window background to transparent
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val messageTextView = dialog.findViewById<TextView>(R.id.dialog_message)
        val negativeButton = dialog.findViewById<TextView>(R.id.button_negative)
        val positiveButton = dialog.findViewById<TextView>(R.id.button_positive)

        messageTextView.text = "Delete this chat?"

        negativeButton.setOnClickListener {
            dialog.dismiss()
        }

        positiveButton.setOnClickListener {
            // Handle positive button click
            chats.deleteChatBoxes()
        }
        dialog.setCancelable(false)
        dialog.show()
    }


    fun updateToolbar(count: Int) {
        toolbar.title = "$count"
        toolbar.setBackgroundColor(Color.rgb(11, 97, 86)) // Use your new color
        toolbar.setTitleTextColor(Color.rgb(255, 255, 255))
        chats = Chats.getInstance()!!
        // Set navigation icon
        toolbar.setNavigationIcon(R.drawable.leading_white_icon)
        toolbar.setNavigationOnClickListener {
            // Handle navigation icon press
            chats.adapterClearSelection()
            defaultToolbar()
        }

        // Invalidate the menu to show new items
        toolbar.invalidateMenu(R.menu.delete_chatbox_menu)

    }

    fun defaultToolbar() {
        toolbar.apply {
            setBackgroundColor(Color.rgb(11, 97, 86)) // Use your new color
            setTitleTextColor(Color.rgb(255, 255, 255))
            title = "WhatsAppClone"
            navigationIcon = null
            // Invalidate the menu to show new items
            invalidateMenu(R.menu.contact_action_btn)
        }
    }
}

private fun Toolbar.invalidateMenu(contactActionBtn: Int) {
    menu.clear()
    inflateMenu(contactActionBtn)
}
