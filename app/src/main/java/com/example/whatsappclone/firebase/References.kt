package com.example.whatsappclone.firebase

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.whatsappclone.HomeActivity
import com.example.whatsappclone.model.ContactSaved
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class References {

    companion object {
        lateinit var contact: ContactSaved
        lateinit var rdb: DatabaseReference
        lateinit var fdb: CollectionReference
        var receiverList = MutableLiveData<List<String>>()

        fun getCurrentUserId() : String{
            return FirebaseAuth.getInstance().currentUser!!.uid
        }

        fun getAllSignInUsers(): CollectionReference {
            val database = FirebaseFirestore.getInstance()
            fdb = database.collection("All_SignIn_Users_Contacts")
            return fdb
        }

        fun getAllContactsInfo(): CollectionReference {
            val database = FirebaseFirestore.getInstance()
            fdb = database.collection("All_Contacts_Info")
            return fdb
        }

        fun getChatsRef() : DatabaseReference {
            val database = FirebaseDatabase.getInstance()
            rdb = database.getReference("All_Users_Chats")
            return rdb
        }

        fun getAllUsersChat(homeActivity: HomeActivity) {
            rdb = getChatsRef()
            rdb.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<String>()
                    if(snapshot.exists() && snapshot.hasChildren()){
                        snapshot.children.forEach { doc ->
                            val part = doc.key!!.split(" ")
                            if(part[0] == References.getCurrentUserId())
                                list.add(doc.key.toString())
                        }
                    }
                    receiverList.postValue(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(homeActivity, "fetching User Chats failed", Toast.LENGTH_SHORT).show()
                }

            })
        }

        fun addNewContact(contact: ContactSaved, callback: (Boolean) -> Unit){
            this.contact = contact
            fdb = getAllContactsInfo()
            fdb.document(contact.userid!!).set(contact).addOnSuccessListener { documentReference ->
                Log.d("Document", "DocumentSnapshot added with ID: $documentReference")
                callback(true)
            }
                .addOnFailureListener { e ->
                    Log.w("Document", "Error adding document", e)
                    callback(false)
                }
        }

        fun getCurrentContact(): CollectionReference {
            val database = FirebaseFirestore.getInstance()
            fdb = database.collection("Current_Contact_Info")
            return fdb
        }

    }
}