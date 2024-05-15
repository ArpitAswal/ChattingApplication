package com.example.whatsappclone.groupchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.NewGroupAdapter
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.model.ContactSaved
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query

class NewGroupActivity : AppCompatActivity() {

    private lateinit var rcv: RecyclerView
    private lateinit var fab: FloatingActionButton
    private var memberList = ArrayList<ContactSaved>()
    override fun onStart() {
        super.onStart()
        getContactsFromFDB()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group)

        setSupportActionBar(findViewById(R.id.group_toolbar))

        // Get the ActionBar
        val actionBar = supportActionBar

        // Set custom leading icon
        actionBar?.setHomeAsUpIndicator(R.drawable.leading_white_icon)

        // Enable the home button
        actionBar?.setDisplayHomeAsUpEnabled(true)
        rcv = findViewById(R.id.group_recView)
        fab = findViewById(R.id.groupFAB)

    }

    private fun getContactsFromFDB() {

        val fdb: CollectionReference = References.getAllContactsInfo()

        fdb.orderBy("firstname", Query.Direction.ASCENDING).orderBy("lastname", Query.Direction.ASCENDING)
            .addSnapshotListener{ value,error-> run {
                val dataList = mutableListOf<ContactSaved>()
                if (value != null) {
                    for (document in value.documents) {
                        val data: ContactSaved? = document.toObject(ContactSaved::class.java)
                        if(!data?.userid.equals(References.getCurrentUserId()))
                            dataList.add(data!!)
                        else{
                            memberList.add(data!!)
                        }
                    }
                }
                val adapter = NewGroupAdapter(dataList)
                rcv.adapter = adapter
                rcv.layoutManager = LinearLayoutManager(this)
                fab.setOnClickListener {
                    val data = memberList[0]
                   memberList = adapter.getDataList()
                    if(memberList.isEmpty()){
                        Toast.makeText(this@NewGroupActivity," At least one contact should be added", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        memberList.add(0,data)
                        val intent = Intent(this, GroupCreationActivity::class.java)
                        intent.putExtra("SelectedMembers", memberList)
                        startActivity(intent)
                    }
                }
                if (error != null) {
                    if (error.message?.isNotEmpty() == true) {
                        // Handle errors
                        Log.i("Exception", error.message.toString())
                    }
                }
            }
            }}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                // Handle search action
                Toast.makeText(this@NewGroupActivity, "Search Item Clicked", Toast.LENGTH_SHORT).show()
                true
            }

            android.R.id.home -> {
                // Handle clicks on the leading icon (e.g., navigate back)
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.contact_action_btn, menu)
        return true
    }

}