package com.example.whatsappclone.groupchat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappclone.HomeActivity
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.ChatAdapter
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.model.ContactSaved
import com.example.whatsappclone.model.MessagesModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChatDetailActivity : AppCompatActivity() {

    private lateinit var name: TextView
    private lateinit var dp: ImageView
    private lateinit var senderId: String
    private lateinit var receiverId: String
    private lateinit var leading: ImageView
    private lateinit var backBtn: ImageView
    private lateinit var selectedCountText: TextView
    private lateinit var deleteIcon: ImageView
    private lateinit var audioIcon: ImageView
    private lateinit var videoIcon: ImageView
    private lateinit var rcv: RecyclerView
    private lateinit var edtMsg: EditText
    private lateinit var sendMsg: FrameLayout
    private lateinit var rdb: DatabaseReference
    private lateinit var authUser: ContactSaved
    private lateinit var adapter: ChatAdapter
    private var messageList = ArrayList<MessagesModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_detail)
        CoroutineScope(Dispatchers.Main).launch {
            // Call your suspend function within the coroutine
            authUser = References.getCurrentAuthUserInfo()!!
        }

        init()
        leading.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        sendMsg.setOnClickListener {
            val msg = edtMsg.text.toString()
            val timestamp = getTimestamp()
            if (msg.isNotEmpty()) {
                val model = MessagesModel(
                    "${authUser.firstname.toString()} ${authUser.lastname.toString()}",
                    authUser.userid!!,
                    msg,
                    timestamp
                )
                rdb.child("$senderId $receiverId").push().setValue(model).addOnSuccessListener {
                    edtMsg.text.clear()
                    if (senderId != receiverId) rdb.child("$receiverId $senderId").push()
                        .setValue(model).addOnSuccessListener {}
                }.addOnFailureListener {}
            }
        }

        rdb.child("$senderId $receiverId").addValueEventListener(object : ValueEventListener {

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
                        if (owner!!.isNotEmpty() && id!!.isNotEmpty() && msg!!.isNotEmpty() && time!!.isNotEmpty()) {
                            val messageModel = MessagesModel(
                                owner, id, msg, time
                            )
                            messageList.add(messageModel)
                            rcv.adapter?.notifyDataSetChanged()
                        }
                    }

                } else {
                    val timestamp = getTimestamp()
                    val model = MessagesModel(
                        "", "", "", timestamp
                    )
                    rdb.child("$senderId $receiverId").child("EmptyValue").setValue(model)
                    rdb.child("$receiverId $senderId").child("EmptyValue").setValue(model)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatDetailActivity, "can't fetch messages", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        deleteIcon.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun getTimestamp(): String {
        val currentTime = Calendar.getInstance().time // Current time as Date object
        val dateFormat =
            SimpleDateFormat("dd MMMM yyyy 'at' HH:mm:ss 'UTC'XXX", Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    override fun onBackPressed() {
        if (deleteIcon.visibility == View.VISIBLE) {
            adapter.clearSelections()
            updateToolbarForDefault()
        } else {
            super.onBackPressed()
        }
    }

    private fun init() {
        val intent = intent
        val username = "${intent.getStringExtra("firstName").toString()} ${
            intent.getStringExtra("lastName").toString()
        }"
        val userDp = intent.getStringExtra("userDP")
        receiverId = intent.getStringExtra("userId").toString()
        senderId = References.getCurrentUserId()
        rdb = References.getChatsRef()
        dp = findViewById(R.id.user_profile)
        name = findViewById(R.id.user_title)
        leading = findViewById(R.id.leading)
        rcv = findViewById(R.id.chat_recview)
        edtMsg = findViewById(R.id.edt_message)
        sendMsg = findViewById(R.id.send_btn)
        backBtn = findViewById(R.id.back_icon)
        selectedCountText = findViewById(R.id.selected_count)
        deleteIcon = findViewById(R.id.delete_icon)
        videoIcon = findViewById(R.id.video_calling)
        audioIcon = findViewById(R.id.audio_calling)

        if (userDp?.isNotEmpty() == true) Glide.with(this).load(userDp).error(R.drawable.avatar)
            .into(dp)
        name.text = username

        adapter = ChatAdapter(messageList)
        rcv.adapter = adapter
        rcv.layoutManager = LinearLayoutManager(this)


        adapter.setOnItemClickListener {
            if (adapter.getSelectedMessages().isEmpty()) {
                updateToolbarForDefault()
            } else {
                updateToolbarForSelection(adapter.getSelectedMessages().size)
            }
        }

        adapter.setOnItemLongClickListener { selectedCount ->
            updateToolbarForSelection(selectedCount)
        }

        backBtn.setOnClickListener {
            adapter.clearSelections()
            updateToolbarForDefault()
        }

    }

    private fun updateToolbarForSelection(selectedCount: Int) {
        leading.visibility = View.GONE
        dp.visibility = View.GONE
        name.visibility = View.GONE
        backBtn.visibility = View.VISIBLE
        deleteIcon.visibility = View.VISIBLE
        selectedCountText.text = selectedCount.toString()
        selectedCountText.visibility = View.VISIBLE
        audioIcon.visibility = View.GONE
        videoIcon.visibility = View.GONE
    }

    private fun updateToolbarForDefault() {
        leading.visibility = View.VISIBLE
        dp.visibility = View.VISIBLE
        name.visibility = View.VISIBLE
        backBtn.visibility = View.GONE
        deleteIcon.visibility = View.GONE
        selectedCountText.visibility = View.GONE
        audioIcon.visibility = View.VISIBLE
        videoIcon.visibility = View.VISIBLE
    }

    private fun showDeleteDialog() {
        val dialog = AlertDialog.Builder(this).setMessage("Delete message?")
            .setPositiveButton("Delete for everyone") { _, _ ->
                deleteSelectedMessages(deleteForEveryone = true)
            }.setNegativeButton("Delete for me") { _, _ ->
                deleteSelectedMessages(deleteForEveryone = false)
            }.setNeutralButton("Cancel", null).create()
        dialog.show()

        // Customize button positions
        val neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

        val params = positiveButton.layoutParams as LinearLayout.LayoutParams
        params.weight = 1f
        positiveButton.layoutParams = params
        neutralButton.layoutParams = params
        negativeButton.layoutParams = params
    }

    private fun deleteSelectedMessages(deleteForEveryone: Boolean) {
        val selectedMessages = adapter.getSelectedMessages().toMutableSet()
        val ref = rdb.child("$senderId $receiverId")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val childCount = snapshot.childrenCount.toInt()
                var count = 0
                for (childSnapshot in snapshot.children) {
                    val chatModel = childSnapshot.getValue(MessagesModel::class.java)!!
                    count++
                    if (selectedMessages.contains(chatModel)) {
                        childSnapshot.ref.removeValue().addOnSuccessListener {
                            adapter.clearSelections()
                            updateToolbarForDefault()
                        }
                    }
                }
                if (count == childCount) {
                    selectedMessages.clear()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        if (deleteForEveryone) {
            val ref2 = rdb.child("$receiverId $senderId")
            val messagesSelected = adapter.getSelectedMessages().toMutableSet()

            ref2.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val childCount = snapshot.childrenCount.toInt()
                    var count = 0
                    for (childSnapshot in snapshot.children) {
                        count++
                        val chatModel = childSnapshot.getValue(MessagesModel::class.java)!!
                        if (messagesSelected.contains(chatModel)) {
                            childSnapshot.ref.removeValue().addOnSuccessListener {}
                        }
                    }
                    if (count == childCount) {
                        messagesSelected.clear()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

}