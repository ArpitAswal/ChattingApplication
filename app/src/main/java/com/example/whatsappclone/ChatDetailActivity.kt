package com.example.whatsappclone

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.system.StructMsghdr
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.example.whatsappclone.adapter.ChatAdapter
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.model.MessagesModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import java.util.Date

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
    private var messageList = ArrayList<MessagesModel>()

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
            if(msg.isNotEmpty()) {
                val model = MessagesModel(
                    msg,
                    senderId,
                    Date().time
                )
                rdb.child("${senderId}${receiverId}").setValue(model)
                    .addOnSuccessListener {
                        edtMsg.text.clear()
                        rdb.child("${receiverId}${senderId}").child("receiver").setValue(model).addOnSuccessListener {

                        }
                    }
            }
        }

        rdb.child("${senderId}${receiverId}").addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                messageList.clear()
                for( data in snapshot.children){
                    val messageModel = data.getValue(MessagesModel::class.java)
                    if (messageModel != null) {
                        messageList.add(messageModel)
                    }
                    rcv.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun init(){
        val intent= intent
        val username = "${intent.getStringExtra("firstName")} ${intent.getStringExtra("lastName")}"
        val userDp =  intent.getStringExtra("userDP")
        receiverId = intent.getStringExtra("userId").toString()
        senderId = References.getCurrentUserId()
        rdb = References.getChatsRef()
        fdb = References.getAllContactsInfo()
        dp = findViewById(R.id.user_profile)
        name = findViewById(R.id.user_title)
        backBtn = findViewById(R.id.leading)
        rcv = findViewById(R.id.chat_recview)
        edtMsg = findViewById(R.id.edt_message)
        sendMsg = findViewById(R.id.send_btn)

        if(userDp?.isNotEmpty() == true)
         Glide.with(this).load(userDp).into(dp)
        name.text = username

        val adapter = ChatAdapter(messageList)
        rcv.adapter = adapter
        rcv.layoutManager = LinearLayoutManager(this)

    }
}