package com.example.whatsappclone.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.CallAdapter
import com.example.whatsappclone.adapter.ContactAdapter
import com.example.whatsappclone.contact.CallingActivity
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.groupchat.ChatDetailActivity
import com.example.whatsappclone.groupchat.NewGroupActivity
import com.example.whatsappclone.model.CallModel
import com.example.whatsappclone.model.ContactSaved
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query

class Calls : Fragment() {


    private lateinit var rcv: RecyclerView
    private lateinit var recent: TextView
    private lateinit var fab: FloatingActionButton
    private var callList = mutableListOf<CallModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calls, container, false)

        val adapter = CallAdapter(callList)
        recent = view.findViewById(R.id.recentTV)
        rcv = view.findViewById(R.id.recyclerViewCall)
        fab = view.findViewById(R.id.fabAddCall)
        rcv.adapter = adapter
        val layout = LinearLayoutManager(view.context)
        rcv.layoutManager = layout

        return view
    }
    override fun onStart() {
        super.onStart()
        getContactsFromDB()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.setOnClickListener {
            val layout: ConstraintLayout = view.findViewById(R.id.call_ConstraintLayout)
            val slideAnimation = AnimationUtils.loadAnimation(view.context, R.anim.slide)
            layout.startAnimation(slideAnimation)

            startActivityForResult(Intent(view.context, CallingActivity::class.java), 101)

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getContactsFromDB() {

        val fdb: CollectionReference = References.getAllCallingInfo()

        fdb.orderBy("dateTime", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                run {
                    callList.clear()
                    if (value != null) {
                        for (document in value.documents) {
                            val data: CallModel? = document.toObject(CallModel::class.java)
                            if (data != null && !data.userId.equals(References.getCurrentUserId())) {
                                callList.add(data)
                            }
                        }
                        if (callList.isNotEmpty()) {
                            recent.visibility = View.VISIBLE
                        } else {
                            recent.visibility = View.GONE
                        }
                        rcv.adapter?.notifyDataSetChanged()
                    }
                    if (error != null) {
                        if (error.message?.isNotEmpty() == true) {
                            // Handle errors
                            Log.i("Exception", error.message.toString())
                        }
                    }
                }
            }
    }

}