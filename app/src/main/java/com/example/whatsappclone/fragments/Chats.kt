package com.example.whatsappclone.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.ChatDetailActivity
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.ContactAdapter
import com.example.whatsappclone.adapter.UserModelAdapter
import com.example.whatsappclone.contact.ContactActivity
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.model.ContactSaved
import com.example.whatsappclone.model.UserModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Chats : Fragment() {
    private var dataList = ArrayList<UserModel>()
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

        References.receiverList.observe(viewLifecycleOwner){
                val fdb = References.getAllContactsInfo()
                val rdb = References.getChatsRef()
                dataList.clear()
                it.forEach{stringData ->
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
                                rdb.child(stringData).get().addOnSuccessListener { value->
                                    if(value!=null){
                                        val firstChild = value.children.last()
                                        val lastmsg = firstChild.child("msg").getValue(String::class.java).toString()
                                        Log.i("lastmsg", lastmsg)
                                        user.userLastMsg = lastmsg
                                        dataList.add(user)
                                        rcv.adapter?.notifyDataSetChanged()
                                    }
                                }
                            }
                            else if(error!=null){
                                Toast.makeText(view.context, error.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }

        }
    }

}