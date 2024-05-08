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
import com.example.whatsappclone.R
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
                it.forEach{stringData ->
                    fdb.whereEqualTo("userid", stringData).addSnapshotListener { value, error ->
                        run {
                            if (value != null) {
                                val contact: ContactSaved = value.toObjects(ContactSaved::class.java)[0]
                                val user = UserModel(
                                    profileImg = contact.dp!!,
                                    username = "${contact.firstname} ${contact.lastname}",
                                    userId = contact.userid!!,
                                    userLastMsg = ""
                                )
                                rdb.child(contact.userid!!).get().addOnSuccessListener { value->
                                    if(value!=null){
                                        val firstChild = value.children.first().children.last()
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