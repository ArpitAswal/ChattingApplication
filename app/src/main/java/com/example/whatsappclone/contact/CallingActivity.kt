package com.example.whatsappclone.contact

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.HomeActivity
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.CallingContactsAdapter
import com.example.whatsappclone.adapter.SelectContactAdapter
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.fragments.Calls
import com.example.whatsappclone.model.CallModel
import com.example.whatsappclone.model.ContactSaved
import com.google.android.material.divider.MaterialDivider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.UUID

class CallingActivity : AppCompatActivity() {

    private lateinit var linear: LinearLayout
    private lateinit var videoCalling: FrameLayout
    private lateinit var audioCalling: FrameLayout
    private lateinit var rcv: RecyclerView
    private lateinit var rcvH: RecyclerView
    private lateinit var divider: MaterialDivider
    private lateinit var noOfCont: TextView
    private lateinit var contactAdapter: CallingContactsAdapter
    private lateinit var selectedContactAdapter: SelectContactAdapter
    private var contactList = mutableListOf<ContactSaved>()
    private var selectedContacts = mutableListOf<ContactSaved>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)

        linear = findViewById(R.id.linearList)
        rcv = findViewById(R.id.contact_recView)
        rcvH = findViewById(R.id.multipleUserCallRCV)
        divider = findViewById(R.id.divider)
        noOfCont = findViewById(R.id.contactXTV)
        videoCalling = findViewById(R.id.videocall_layout)
        audioCalling = findViewById(R.id.audiocall_layout)

        videoCalling.setOnClickListener {
        saveCallingContacts(selectedContacts,"Video")
        }

        audioCalling.setOnClickListener {
            saveCallingContacts(selectedContacts,"Audio")
        }
        contactAdapter = CallingContactsAdapter(contactList) { contact, position ->
            if (selectedContacts.contains(contact)) {
                selectedContacts.remove(contact)
                contactAdapter.toggleSelection(contact)
                selectedContactAdapter.removeItem(contact)

            } else {
                selectedContacts.add(contact)
                contactAdapter.toggleSelection(contact)
            }
            selectedContactAdapter.notifyDataSetChanged()
            updateHorizontalRecyclerViewVisibility()
        }

        selectedContactAdapter = SelectContactAdapter(selectedContacts) { contact ->
            selectedContacts.remove(contact)
            selectedContactAdapter.removeItem(contact)
            contactAdapter.toggleSelection(contact)
            updateHorizontalRecyclerViewVisibility()
        }

        rcv.layoutManager = LinearLayoutManager(this)
        rcv.adapter = contactAdapter

        rcvH.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rcvH.adapter = selectedContactAdapter

        updateHorizontalRecyclerViewVisibility()
    }

    private fun saveCallingContacts(selectedContacts: MutableList<ContactSaved>, s: String) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("d MMM, h:mm a", Locale.getDefault())
        val time = dateFormat.format(calendar.time)
        val fdb = References.getAllCallingInfo()
        for(i in 1..selectedContacts.size) {
            val uniqueCallId = UUID.randomUUID().toString()
            val contact = selectedContacts[i-1]
            val callData = CallModel(
                contact.firstname!!,
                contact.userid!!,
                contact.dp!!,
                time,
                s
            )
            fdb.document(uniqueCallId).set(callData).addOnSuccessListener {
            if(i == selectedContacts.size-1){
                Toast.makeText(this@CallingActivity, "Your $s call has been ended", Toast.LENGTH_SHORT).show()
                navigate()
            }

            }.addOnFailureListener {
                Toast.makeText(this@CallingActivity, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun navigate() {
        selectedContacts.clear()
        contactAdapter.clearAllSelectContacts()
        selectedContactAdapter.notifyDataSetChanged()
        linear.visibility = View.GONE
        val intent = Intent("NAVIGATE_TO_FRAGMENT")
        intent.putExtra("TARGET_FRAGMENT_INDEX", 2) // Change 2 to the index of your desired fragment
        sendBroadcast(intent)
        finish()
    }

    private fun updateHorizontalRecyclerViewVisibility() {
        if (selectedContacts.isEmpty()) {
            linear.visibility = View.GONE
            noOfCont.visibility = View.VISIBLE
        } else {
            linear.visibility = View.VISIBLE
            noOfCont.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        getContactsFromFDB()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getContactsFromFDB() {

        val fdb: CollectionReference = References.getAllContactsInfo()

        fdb.orderBy("firstname", Query.Direction.ASCENDING)
            .orderBy("lastname", Query.Direction.ASCENDING).addSnapshotListener { value, error ->
                run {

                    if (value != null) {
                        contactList.clear()
                        for (document in value.documents) {
                            val data: ContactSaved? = document.toObject(ContactSaved::class.java)
                            if (!data?.userid.equals(References.getCurrentUserId())) contactList.add(
                                data!!
                            )
                        }
                        rcv.adapter?.notifyDataSetChanged()
                        noOfCont.text = "Add up to ${value.documents.size} people"
                    }
                    if (error != null) {
                        if (error.message?.isNotEmpty() == true) {
                            // Handle errors
                            Toast.makeText(this@CallingActivity, error.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }
}