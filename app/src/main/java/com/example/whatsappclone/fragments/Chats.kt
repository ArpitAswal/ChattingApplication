package com.example.whatsappclone.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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

class Chats : Fragment() {
    private var dataList = mutableListOf<UserModel>()
    private lateinit var rcv: RecyclerView
    private lateinit var mAddFab: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        // Inflate the layout for this fragment
        rcv = view.findViewById(R.id.recView)
        val adapter = UserModelAdapter(dataList)
        rcv.adapter = adapter
        val layout = LinearLayoutManager(view.context)
        rcv.layoutManager = layout
        adapter.setOnClickListener(object :
            UserModelAdapter.OnClickListener {
            override fun onClick(position: Int, individualUser: UserModel) {
                val intent = Intent(view.context, ChatDetailActivity::class.java)
                intent.putExtra("userId", individualUser.userId)
                intent.putExtra("firstName", individualUser.username)
                intent.putExtra("lastName", "")
                intent.putExtra("userDP", individualUser.profileImg)
                startActivity(intent)
            }
        })

        adapter.setOnGroupClickListener(object :
            UserModelAdapter.OnGroupClickListener {
            override fun onGroupClick(position: Int, individualUser: UserModel) {
                val intent = Intent(view.context, GroupChatDetailActivity::class.java)
                intent.putExtra("GroupId", individualUser.userId)
                intent.putExtra("GroupName", individualUser.username)
                intent.putExtra("GroupImage", individualUser.profileImg)
                startActivity(intent)

            }
        }
        )
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        mAddFab = view.findViewById(R.id.FAB)
        mAddFab.setOnClickListener {
            val layout: ConstraintLayout = view.findViewById(R.id.chatsLayout)
            val slideAnimation = AnimationUtils.loadAnimation(view.context, R.anim.slide)
            layout.startAnimation(slideAnimation)

            startActivity(Intent(view.context, ContactActivity::class.java))
        }
        val mediatorLiveData = MediatorLiveData<List<UserModel>>()

        val receiverDataList = mutableListOf<UserModel>()
        val groupsDataList = mutableListOf<UserModel>()

        mediatorLiveData.addSource(References.receiverList) { receiverList ->
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
                            source = ListType.Individual
                        )

                        rdb.child(stringData).get().addOnSuccessListener { chatSnapshot ->
                            if (chatSnapshot != null) {
                                val firstChild = chatSnapshot.children.last()
                                val lastMsg =
                                    firstChild.child("msg").getValue(String::class.java).toString()
                                user.userLastMsg = lastMsg
                                receiverDataList.add(user) // Add the updated user to the receiverDataList
                                mediatorLiveData.value =
                                    receiverDataList // Update mediatorLiveData with the new list
                            }
                        }
                    } else if (error != null) {
                        // Handle error
                        Toast.makeText(view.context, error.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        mediatorLiveData.addSource(References.groupsList) { groupsList ->
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
                                source = ListType.Group
                            )

                            rdb.child(stringData).get().addOnSuccessListener { chatSnapshot ->
                                if (chatSnapshot != null) {
                                    val firstChild = chatSnapshot.children.last()
                                    val lastMsg =
                                        firstChild.child("msg").getValue(String::class.java)
                                            .toString()
                                    user.userLastMsg = lastMsg
                                    groupsDataList.add(user) // Add the updated user to the groupsDataList
                                    mediatorLiveData.value =
                                        groupsDataList // Update mediatorLiveData with the new list
                                }
                            }
                        }
                    }
                }
            }
        }

        mediatorLiveData.observe(viewLifecycleOwner) {
            // Combine receiverDataList and groupsDataList and update RecyclerView adapter with combined list
            dataList.clear()
            dataList.addAll(receiverDataList)
            dataList.addAll(groupsDataList)
            rcv.adapter?.notifyDataSetChanged() // Assuming you're using ListAdapter or RecyclerView.Adapter with DiffUtil
        }

    }
}


/*
        References.receiverList.observe(viewLifecycleOwner) {
            val fdb = References.getAllContactsInfo()
            val rdb = References.getChatsRef()
            dataList.clear()
            it.forEach { stringData ->
                val part = stringData.split(" ")[1]
                fdb.whereEqualTo("userid", part).addSnapshotListener { value, error ->
                    run {
                        if (value != null) {
                            val contact: ContactSaved = value.toObjects(ContactSaved::class.java)[0]
                            val user = UserModel(
                                profileImg = contact.dp!!,
                                username = "${contact.firstname} ${contact.lastname}",
                                userId = contact.userid!!,
                                userLastMsg = ""
                            )
                            rdb.child(stringData).get().addOnSuccessListener { value ->
                                if (value != null) {
                                    val firstChild = value.children.last()
                                    val lastmsg =
                                        firstChild.child("msg").getValue(String::class.java)
                                            .toString()
                                    Log.i("lastmsg", lastmsg)
                                    user.userLastMsg = lastmsg
                                    dataList.add(user)
                                    rcv.adapter?.notifyDataSetChanged()
                                }
                            }
                        } else if (error != null) {
                            Toast.makeText(
                                view.context,
                                error.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

        }

        References.groupsList.observe(viewLifecycleOwner) {
            val fdb = References.getAllGroupsInfo()
            val rdb = References.getGroupRef()
            dataList.clear()
            it.forEach { stringData ->
                fdb.document(stringData).get().addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Convert Firestore document to your data model
                        val groupModel = document.toObject(GroupModel::class.java)
                        // Handle retrieved data
                        if (groupModel != null) {
                            // Handle the retrieved group data
                            val user = UserModel(
                                profileImg = groupModel.groupProfile!!,
                                userId = groupModel.groupId!!,
                                username = groupModel.groupName!!,
                                userLastMsg = ""
                            )

                            rdb.child(stringData).get().addOnSuccessListener { value ->
                                if (value != null) {
                                    val firstChild = value.children.last()
                                    val lastmsg =
                                        firstChild.child("msg").getValue(String::class.java)
                                            .toString()
                                    Log.i("lastmsg", lastmsg)
                                    user.userLastMsg = lastmsg
                                    dataList.add(user)
                                    rcv.adapter?.notifyDataSetChanged()
                                }
                            }
                        } else {
                            // Document doesn't exist or couldn't be converted to GroupModel
                            Toast.makeText(
                                view.context,
                                "Document doesn't exist or couldn't be converted to GroupModel",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Document doesn't exist or there was an error fetching the document
                        Toast.makeText(
                            view.context,
                            "Document doesn't exist or there was an error fetching the document",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                    .addOnFailureListener { exception: Exception ->
                        // Handle any errors
                        Toast.makeText(
                            view.context,
                            exception.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                    }

            }
        }

        */