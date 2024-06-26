package com.example.whatsappclone.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.groupchat.ChatDetailActivity
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.UserModelAdapter
import com.example.whatsappclone.contact.ContactActivity
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.groupchat.GroupChatDetailActivity
import com.example.whatsappclone.model.ContactSaved
import com.example.whatsappclone.model.GroupModel
import com.example.whatsappclone.model.ListType
import com.example.whatsappclone.model.UserModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Chats : Fragment() {
    private var dataList = mutableListOf<UserModel>()
    private val mediatorLiveData = MediatorLiveData<List<UserModel>>()
    private val receiverDataList = mutableListOf<UserModel>()
    private val groupsDataList = mutableListOf<UserModel>()
    private lateinit var rcv: RecyclerView
    private lateinit var mAddFab: FloatingActionButton
    private val addedSources = mutableSetOf<LiveData<*>>()
    private var count = 0

    private val receiverObserver = { receiverList: List<String> ->
        receiverDataList.clear() // Clear the list before updating with new data

        val fdb = References.getAllContactsInfo()
        val rdb = References.getChatsRef()

        receiverList.forEach { stringData ->
            val part = stringData.split(" ")[1]
            fdb.whereEqualTo("userid", part).addSnapshotListener { value, error ->
                if (value != null) {
                    val contact: ContactSaved = value.toObjects(ContactSaved::class.java)[0]
                    val user = UserModel(
                        profileImg = contact.dp!!,
                        username = "${contact.firstname} ${contact.lastname}",
                        userId = contact.userid!!,
                        userLastMsg = "",
                        source = ListType.Individual,
                        chatTime = ""
                    )

                    rdb.child(stringData).get().addOnSuccessListener { chatSnapshot ->
                        if (chatSnapshot != null) {
                            val firstChild =
                                chatSnapshot.children.reversed()[if (chatSnapshot.children.toList().size > 1) 1 else 0]
                            val lastMsg =
                                firstChild.child("msg").getValue(String::class.java).toString()
                            user.userLastMsg = lastMsg
                            user.chatTime = firstChild.child("time").getValue(String::class.java)!!

                            receiverDataList.add(user) // Add the updated user to the receiverDataList
                            mediatorLiveData.value = receiverDataList
                        }
                    }
                } else if (error != null) {
                    // Handle error
                    Toast.makeText(this.context, error.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private val groupsObserver = { groupsList: List<String> ->
        groupsDataList.clear() // Clear the list before updating with new data

        val fdb = References.getAllGroupsInfo()
        val rdb = References.getGroupRef()

        groupsList.forEach { stringData ->
            fdb.document(stringData).get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val groupModel = document.toObject(GroupModel::class.java)
                    if (groupModel != null) {
                        val user = UserModel(
                            profileImg = groupModel.groupProfile!!,
                            userId = groupModel.groupId!!,
                            username = groupModel.groupName!!,
                            userLastMsg = "",
                            source = ListType.Group,
                            chatTime = ""
                        )

                        rdb.child(stringData).get().addOnSuccessListener { chatSnapshot ->
                            if (chatSnapshot != null) {
                                val childrenList = chatSnapshot.children.toList()
                                for (lastChild in childrenList.reversed()) {
                                    user.userLastMsg =
                                        lastChild.child("msg").getValue(String::class.java)
                                            .toString()
                                    user.chatTime =
                                        lastChild.child("time").getValue(String::class.java)!!
                                    if (user.userLastMsg.isNotEmpty()) break
                                }
                                groupsDataList.add(user) // Add the updated user to the groupsDataList
                                mediatorLiveData.value = groupsDataList
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        rcv = view.findViewById(R.id.recView)
        val adapter = UserModelAdapter(dataList)
        rcv.adapter = adapter
        val layout = LinearLayoutManager(view.context)
        rcv.layoutManager = layout
        adapter.setOnClickListener(object : UserModelAdapter.OnClickListener {
            override fun onClick(position: Int, individualUser: UserModel) {
                val intent = Intent(view.context, ChatDetailActivity::class.java)
                intent.putExtra("userId", individualUser.userId)
                intent.putExtra("firstName", individualUser.username)
                intent.putExtra("lastName", "")
                intent.putExtra("userDP", individualUser.profileImg)
                startActivity(intent)
            }
        })

        adapter.setOnGroupClickListener(object : UserModelAdapter.OnGroupClickListener {
            override fun onGroupClick(position: Int, individualUser: UserModel) {
                val intent = Intent(view.context, GroupChatDetailActivity::class.java)
                intent.putExtra("GroupId", individualUser.userId)
                intent.putExtra("GroupName", individualUser.username)
                intent.putExtra("GroupImage", individualUser.profileImg)
                startActivity(intent)
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAddFab = view.findViewById(R.id.FAB)
        mAddFab.setOnClickListener {
            val layout: ConstraintLayout = view.findViewById(R.id.chatsLayout)
            val slideAnimation = AnimationUtils.loadAnimation(view.context, R.anim.slide)
            layout.startAnimation(slideAnimation)

            startActivity(Intent(view.context, ContactActivity::class.java))
        }

        addSourcesIfNotAdded()
    }

    private fun addSourcesIfNotAdded() {
        if (!addedSources.contains(References.receiverList)) {
            mediatorLiveData.addSource(References.receiverList, receiverObserver)
            addedSources.add(References.receiverList)
        }

        if (!addedSources.contains(References.groupsList)) {
            mediatorLiveData.addSource(References.groupsList, groupsObserver)
            addedSources.add(References.groupsList)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        count++
        mediatorLiveData.observe(viewLifecycleOwner) {
            // Combine receiverDataList and groupsDataList and update RecyclerView adapter with combined list
            val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy 'at' HH:mm:ss 'UTC'XXX")
            dataList.clear()
            dataList.addAll(receiverDataList)
            dataList.addAll(groupsDataList)
            dataList.sortByDescending {
                ZonedDateTime.parse(it.chatTime, formatter)
            }
            rcv.adapter?.notifyDataSetChanged()
        }
    }
}
