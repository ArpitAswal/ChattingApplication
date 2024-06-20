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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class References {

    companion object {
        lateinit var contact: ContactSaved
        lateinit var rdb: DatabaseReference
        lateinit var fdb: CollectionReference
        var receiverList = MutableLiveData<List<String>>()
        var groupsList = MutableLiveData<List<String>>()

        fun getCurrentUserId(): String {
            return FirebaseAuth.getInstance().currentUser!!.uid
        }

        suspend fun getCurrentAuthUserInfo(): ContactSaved? {
            return withContext(Dispatchers.IO) {
                try {
                    val querySnapshot =
                        getAllContactsInfo().whereEqualTo("userid", getCurrentUserId()).get()
                            .await()
                    if (!querySnapshot.isEmpty) {
                        querySnapshot.toObjects(ContactSaved::class.java)[0]
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
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

        fun getAllGroupsInfo(): CollectionReference {
            val database = FirebaseFirestore.getInstance()
            fdb = database.collection("All_Groups_Info")
            return fdb
        }

        fun getChatsRef(): DatabaseReference {
            val database = FirebaseDatabase.getInstance()
            rdb = database.getReference("All_Users_Chats")
            return rdb
        }

        fun getGroupRef(): DatabaseReference {
            val database = FirebaseDatabase.getInstance()
            rdb = database.getReference("All_Groups_Chats")
            return rdb
        }

        fun getAllUsersChat(homeActivity: HomeActivity) {
            rdb = getChatsRef()
            rdb.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<String>()
                    if (snapshot.exists() && snapshot.hasChildren()) {
                        snapshot.children.forEach { doc ->
                            val part = doc.key!!.split(" ")
                            if (part[0] == getCurrentUserId())
                                list.add(doc.key.toString())
                        }
                    }
                    receiverList.postValue(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(homeActivity, "fetching User Chats failed", Toast.LENGTH_SHORT)
                        .show()
                }

            })
        }

        fun getAllGroupsChat(homeActivity: HomeActivity) {
            rdb = getGroupRef()
            rdb.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<String>()
                    if (snapshot.exists() && snapshot.hasChildren()) {
                        snapshot.children.forEach { doc ->
                            val partLength = 28

                            // Split the string into parts of specified length
                            val splitParts = mutableListOf<String>()
                            var startIndex = 0

                            while (startIndex < doc.key!!.length) {
                                val endIndex = startIndex + partLength
                                if (endIndex <= doc.key!!.length) {
                                    // Add the part to the list if endIndex is within the string length
                                    splitParts.add(doc.key!!.substring(startIndex, endIndex))
                                } else {
                                    // Add the remaining characters to the last part if endIndex exceeds the string length
                                    splitParts.add(doc.key!!.substring(startIndex))
                                }
                                startIndex = endIndex
                            }
                            for (part in splitParts) {
                                if (part == getCurrentUserId())
                                    list.add(doc.key.toString())
                            }
                        }
                    }
                    groupsList.postValue(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(homeActivity, "fetching User Chats failed", Toast.LENGTH_SHORT)
                        .show()
                }

            })
        }

        fun addNewContact(contact: ContactSaved, callback: (Boolean) -> Unit) {
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

        fun getStatusReference(): CollectionReference {
            val db = FirebaseFirestore.getInstance()
            fdb = db.collection("All_Users_Status")
            return fdb
        }

        fun statusRemoveFromDB(userId: String?) {
            fdb = getStatusReference()
            fdb.document(userId!!).delete()
        }

        fun getAllCallingInfo(): CollectionReference {
            val database = FirebaseFirestore.getInstance()
            fdb = database.collection("All_Calling_Info").document(References.getCurrentUserId()).collection("UserCalls")
            return fdb
        }
    }
}