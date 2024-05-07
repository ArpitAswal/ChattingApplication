package com.example.whatsappclone.firebase

import android.util.Log
import com.example.whatsappclone.model.ContactSaved
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class References {

    companion object {
        lateinit var contact: ContactSaved
        lateinit var rdb: DatabaseReference
        lateinit var fdb: CollectionReference

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

        fun getAllChatsContact(): CollectionReference {
            val database = FirebaseFirestore.getInstance()
            fdb = database.collection("All_Chats_Users")
            return fdb
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

        fun setCurrentContact(): CollectionReference {
            val database = FirebaseFirestore.getInstance()
            fdb = database.collection("Current_Contact_Info")
            return fdb
        }

    }
}