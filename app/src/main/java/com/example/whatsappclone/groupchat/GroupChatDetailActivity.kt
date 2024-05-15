package com.example.whatsappclone.groupchat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappclone.HomeActivity
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.GroupChatAdapter
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.model.ContactSaved
import com.example.whatsappclone.model.GroupModel
import com.example.whatsappclone.model.MessagesModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class GroupChatDetailActivity : AppCompatActivity() {

    private lateinit var groupName: TextView
    private lateinit var groupMembersName: TextView
    private lateinit var groupDp: ImageView
    private lateinit var leadingBtn: ImageView
    private lateinit var editMsg: EditText
    private lateinit var msgSend: FrameLayout
    private lateinit var rcv: RecyclerView
    private lateinit var rdb: DatabaseReference
    private lateinit var fdb: CollectionReference
    private lateinit var authUser: ContactSaved
    private var messageList = ArrayList<MessagesModel>()
    private var membersList = ArrayList<ContactSaved>()
    private var grpId = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat_detail_actvity)
        CoroutineScope(Dispatchers.Main).launch {
            // Call your suspend function within the coroutine
            authUser = References.getCurrentAuthUserInfo()!!

        }
        init()
        msgSend.setOnClickListener {
            val msg = editMsg.text.toString()
            val currentTime = LocalTime.now()
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm a")
            val formattedTime = currentTime.format(timeFormatter)
            if (msg.isNotEmpty()) {
                val model = MessagesModel(
                    "${authUser.firstname.toString()} ${authUser.lastname.toString()}",
                    authUser.userid!!,
                    msg,
                    formattedTime
                )
                rdb.child(grpId).push().setValue(model)
                    .addOnSuccessListener {
                        editMsg.text.clear()
                    }.addOnFailureListener {
                    }
            }
        }

        leadingBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getChats() {
        rdb.child(grpId).addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                messageList.clear()
                if (snapshot.hasChildren()) {
                    for (data in snapshot.children) {
                        val owner: String? = data.child("owner").getValue(String::class.java)
                        val id: String? = data.child("id").getValue(String::class.java)
                        val msg: String? = data.child("msg").getValue(String::class.java)
                        val time: String? = data.child("time").getValue(String::class.java)
                        val messageModel = MessagesModel(
                            owner!!, id!!, msg!!, time!!
                        )
                        messageList.add(messageModel)
                    }
                    rcv.adapter?.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@GroupChatDetailActivity,
                    "can't fetch messages",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun init() {
        groupName = findViewById(R.id.group_title)
        groupMembersName = findViewById(R.id.group_subtitle)
        groupDp = findViewById(R.id.groupDp)
        val intent = intent
        val selected = (intent.getSerializableExtra("SelectedMembers") as? ArrayList<ContactSaved>)
        groupName.text = intent.getStringExtra("GroupName")
        grpId = intent.getStringExtra("GroupId")!!
        val imageRef = intent.getStringExtra("GroupImage")
        if (!imageRef.isNullOrEmpty()) {
            Glide.with(this).load(imageRef).error(R.drawable.avatar).into(groupDp)
        }
        var names = ""
        if (selected.isNullOrEmpty()) {
            fdb = References.getAllGroupsInfo()

            fdb.whereEqualTo("groupId", grpId).addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(
                        this@GroupChatDetailActivity,
                        error.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addSnapshotListener
                }

                if (value != null) {
                    // Clear the existing membersList before adding new data
                    membersList.clear()

                    val group = value.toObjects(GroupModel::class.java)[0]
                    val members = group.groupMembers.sortedBy {
                        it.firstname
                    }
                    for (users in members) {
                        membersList.add(users)
                        names += if (users.userid == References.getCurrentUserId()) {
                            "You, "
                        } else {
                            "${users.firstname}, "
                        }
                    }
                    names = names.trim()
                    names = names.subSequence(0,names.length-1).toString()
                    groupMembersName.text = names
                    getChats()
                }
            }
        } else {
            membersList = selected
            for (users in membersList) {
                names += if (users.userid == References.getCurrentUserId()) {
                    "You"
                } else {
                    ", ${users.firstname}"
                }
            }
        }
        editMsg = findViewById(R.id.groupEdit_message)
        msgSend = findViewById(R.id.groupSend_btn)
        rcv = findViewById(R.id.groupchat_recview)
        val adapter = GroupChatAdapter(messageList)
        rcv.adapter = adapter
        rcv.layoutManager = LinearLayoutManager(this)
        rdb = References.getGroupRef()
        leadingBtn = findViewById(R.id.group_leading_icon)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (!membersList.isNullOrEmpty()) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
    }
}
