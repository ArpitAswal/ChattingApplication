package com.example.whatsappclone.contact

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.ChatDetailActivity
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.ContactAdapter
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.model.ContactSaved
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query

class ContactActivity : AppCompatActivity() {

    private lateinit var rcv: RecyclerView
    private lateinit var contact: LinearLayout
    private lateinit var snackbar: CoordinatorLayout
    private lateinit var currentContact : ContactSaved
    override fun onStart() {
        super.onStart()
        getCurrentContact()
    }

    private fun getCurrentContact() {
        val fdb: CollectionReference = References.setCurrentContact()
        fdb.document(References.getCurrentUserId()).addSnapshotListener { value, error ->
            run {
                if (value != null) {
                    currentContact = value.toObject(ContactSaved::class.java)!!
                    getContactsFromFDB()

                } else if (error != null) {
                    if (error.message?.isNotEmpty() == true) {
                        // Handle errors
                        Log.i("Exception", error.message.toString())
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        setSupportActionBar(findViewById(R.id.contact_toolbar))

        // Get the ActionBar
        val actionBar = supportActionBar

        // Set custom leading icon
        actionBar?.setHomeAsUpIndicator(R.drawable.leading_white_icon)

        // Enable the home button
        actionBar?.setDisplayHomeAsUpEnabled(true)
        rcv = findViewById(R.id.contacts_recView)
        contact = findViewById(R.id.newcontact_layout)
        snackbar = findViewById(R.id.snackbar_layout)

        // Retrieve message from the intent
        val message = intent.getStringExtra("message")

        // Display the Snackbar
        message?.let {
            Snackbar.make(
                snackbar,
                it,
                Snackbar.LENGTH_SHORT
            ).setAction(
                "UNDO", View.OnClickListener {
                    Toast.makeText(this@ContactActivity, "The contact did not saved", Toast.LENGTH_SHORT).show();
                }
            ).setDuration(2000).show()
        }

        contact.setOnClickListener {
            val layout: ConstraintLayout = findViewById(R.id.contact_activity)
            val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.slide)
            layout.startAnimation(slideAnimation)

            startActivityForResult(Intent(this@ContactActivity, NewContactActivity::class.java), 101)
        }

    }

    private fun getContactsFromFDB() {

        val fdb: CollectionReference = References.getAllContactsInfo()

        fdb.orderBy("firstname", Query.Direction.ASCENDING).orderBy("lastname", Query.Direction.ASCENDING)
            .addSnapshotListener{ value,error-> run {
                val dataList = mutableListOf<ContactSaved>()
                if (value != null) {
                    for (document in value.documents) {
                        val data: ContactSaved? = document.toObject(ContactSaved::class.java)
                        dataList.add(data!!)
                    }
                }
                if(dataList.isEmpty()){
                    dataList.add(currentContact)
                }
                else {
                    dataList[0] = currentContact
                }
                val adapter = ContactAdapter(dataList)
                rcv.adapter = adapter
                rcv.layoutManager = LinearLayoutManager(this)
                adapter.setOnClickListener(object :
                        ContactAdapter.OnClickListener {
                        override fun onClick(position: Int, individualUser: ContactSaved) {
                            val intent = Intent(this@ContactActivity, ChatDetailActivity::class.java)
                            intent.putExtra("userId", individualUser.userid)
                            intent.putExtra("firstName", individualUser.firstname)
                            intent.putExtra("lastName", individualUser.lastname)
                            intent.putExtra("userDP", individualUser.dp)
                            startActivity(intent)
                            finish()
                        }
                    })
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
                Toast.makeText(this@ContactActivity, "Search Item Clicked", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.action_more -> {
                // Handle more options action
                Toast.makeText(this@ContactActivity, "More Item Clicked", Toast.LENGTH_SHORT).show()
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