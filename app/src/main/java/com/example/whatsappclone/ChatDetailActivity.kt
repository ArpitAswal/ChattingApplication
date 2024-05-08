package com.example.whatsappclone

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
import com.example.whatsappclone.adapter.ChatAdapter
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.model.MessagesModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.firestore.CollectionReference
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class ChatDetailActivity : AppCompatActivity() {

    private lateinit var name: TextView
    private lateinit var dp: ImageView
    private lateinit var senderId: String
    private lateinit var receiverId: String
    private lateinit var backBtn: ImageView
    private lateinit var rcv: RecyclerView
    private lateinit var edtMsg: EditText
    private lateinit var sendMsg: FrameLayout
    private lateinit var rdb: DatabaseReference
    private lateinit var fdb: CollectionReference
    private var chatsId = ArrayList<String>()
    private var messageList = ArrayList<MessagesModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_detail)

        init()

        backBtn.setOnClickListener {
            val intent = Intent(this@ChatDetailActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        sendMsg.setOnClickListener {
            val msg = edtMsg.text.toString()
            val currentDate = LocalDate.now()
            val currentDay = currentDate.dayOfWeek
            val currentTime = LocalTime.now()
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm a")
            val formattedDate = currentDate.format(dateFormatter)
            val formattedTime = currentTime.format(timeFormatter)
            if(msg.isNotEmpty()) {
                chatsWithUserId()
                val model = MessagesModel(
                    senderId,
                    msg,
                    formattedTime
                )
                rdb.child(senderId).child("$senderId $receiverId").push().setValue(model)
                    .addOnSuccessListener {
                        edtMsg.text.clear()
                        if(senderId != receiverId)
                          rdb.child(senderId).child("$receiverId $senderId").push().setValue(model).addOnSuccessListener {
                        }
                    }.addOnFailureListener {

                    }
            }
        }

        rdb.child(senderId).child("$senderId $receiverId").addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                messageList.clear()
                if(snapshot.hasChildren()){
                    for(data in snapshot.children){
                        val id: String? = data.child("id").getValue(String::class.java)
                        val msg: String? = data.child("msg").getValue(String::class.java)
                        val time: String? = data.child("time").getValue(String::class.java)
                        val messageModel = MessagesModel(
                            id!!, msg!!, time!!
                        )
                        messageList.add(messageModel)
                        rcv.adapter?.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatDetailActivity, "can't fetch messages", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun chatsWithUserId() {
        val id = hashMapOf(
            "receiverId" to receiverId,
        )
        fdb.document("Chats_With_UserId").collection(senderId).add(id).addOnSuccessListener {
                    chatsId.add(receiverId)
        }.addOnFailureListener {
              Toast.makeText(this@ChatDetailActivity, "can not add receiver ID", Toast.LENGTH_SHORT).show()
        }

    }

    private fun init(){
        val intent= intent
        val username = "${intent.getStringExtra("firstName")} ${intent.getStringExtra("lastName")}"
        val userDp =  intent.getStringExtra("userDP")
        receiverId = intent.getStringExtra("userId").toString()
        senderId = References.getCurrentUserId()
        rdb = References.getChatsRef()
        fdb = References.getCurrentContact()
        dp = findViewById(R.id.user_profile)
        name = findViewById(R.id.user_title)
        backBtn = findViewById(R.id.leading)
        rcv = findViewById(R.id.chat_recview)
        edtMsg = findViewById(R.id.edt_message)
        sendMsg = findViewById(R.id.send_btn)

        if(userDp?.isNotEmpty() == true)
         Glide.with(this).load(userDp).error(R.drawable.avatar).into(dp)
        name.text = username

        val adapter = ChatAdapter(messageList)
        rcv.adapter = adapter
        rcv.layoutManager = LinearLayoutManager(this)

    }
}