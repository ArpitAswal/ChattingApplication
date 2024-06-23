package com.example.whatsappclone.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.StatusAdapter
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.model.ContactSaved
import com.example.whatsappclone.model.StatusModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Calendar

class Status : Fragment() {

    private val capture = 1
    private val pick = 2
    private lateinit var recent: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddStatus: FloatingActionButton
    private lateinit var camera: FrameLayout
    private lateinit var gallery: FrameLayout
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView: View
    private lateinit var fdb: CollectionReference
    private lateinit var authDp: ImageView
    private var authInfo: ContactSaved? = null
    private var statusList = mutableListOf<StatusModel>()
    private lateinit var statusAdapter: StatusAdapter
    private var imageRef = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_status, container, false)

        recent = view.findViewById(R.id.recentStatus)
        recyclerView = view.findViewById(R.id.recyclerViewStatus)
        fabAddStatus = view.findViewById(R.id.fabAddStatus)
        bottomSheetDialog = BottomSheetDialog(view.context)
        bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet, null)
        gallery = bottomSheetView.findViewById(R.id.gallery_layout)
        camera = bottomSheetView.findViewById(R.id.camera_layout)
        authDp = view.findViewById(R.id.authStatus)
        statusAdapter = StatusAdapter(statusList)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = statusAdapter
        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fdb = References.getStatusReference()
        CoroutineScope(Dispatchers.Main).launch {
            // Call your suspend function within the coroutine
            authInfo = References.getCurrentAuthUserInfo()!!
            Glide.with(view.context).load(authInfo!!.dp).error(R.drawable.avatar).into(authDp)

            fdb.orderBy("time").addSnapshotListener { value, error ->
                if (error != null) {
                    // Handle error
                    Toast.makeText(view.context, error.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                } else if (value != null) {
                    statusList.clear()
                    value.documents.forEach { document ->
                        try {
                            val currentTime = Calendar.getInstance().time
                            val status = document.toObject(StatusModel::class.java)
                            if (status != null) {
                                val statusTime = status.time!!.toDate()
                                val differenceInMillis = currentTime.time - statusTime.time
                                val hoursDifference = differenceInMillis / (1000 * 60 * 60)
                                if (hoursDifference >= 24) {
                                    Glide.with(view.context).load(authInfo?.dp).error(R.drawable.avatar).into(authDp)
                                    References.statusRemoveFromDB(status.userId)
                                } else {
                                    if (status.userId.equals(authInfo?.userid)) {
                                        Glide.with(view.context).load(status.imageUrl.toString())
                                            .error(R.drawable.avatar).into(authDp)
                                    } else {
                                        statusList.add(status)
                                    }
                                }
                                if (statusList.isNotEmpty()) {
                                    recent.visibility = View.VISIBLE
                                } else {
                                    recent.visibility = View.GONE
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(view.context, e.message.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    statusAdapter.notifyDataSetChanged()
                }
            }

        }

        // Set up click listener for FAB to add new status
        fabAddStatus.setOnClickListener {
            // Navigate to activity to add new status
            // You can implement the logic to add a new status here
            bottomSheetDialog.setContentView(bottomSheetView)
            camera.setOnClickListener {
                dispatchTakePictureIntent()
                bottomSheetDialog.dismiss()
            }

            gallery.setOnClickListener {
                openGallery()
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.show()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            startActivityForResult(takePictureIntent, capture)
        }
    }

    private fun openGallery() {
        Intent(
            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).also { pickPhotoIntent ->
            pickPhotoIntent.type = "image/*"
            startActivityForResult(pickPhotoIntent, pick)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val currentTime = Calendar.getInstance().time // Current time as Date object
            val timestamp = Timestamp(currentTime)

            when (requestCode) {
                capture -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    //authDp.setImageBitmap(imageBitmap)
                    // Do something with the captured image bitmap
                    uploadBitmapToFirebase(imageBitmap, timestamp)
                }

                pick -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        //authDp.setImageURI(selectedImageUri)
                        uploadImageToFirebase(selectedImageUri, timestamp)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadImageToFirebase(imageUri: Uri, statusTime: Timestamp) {

        val storageRef =
            FirebaseStorage.getInstance().reference.child("Status").child(authInfo?.userid!!)
        val uploadTask = storageRef.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            // Image uploaded successfully
            // Now you can retrieve the download URL to use in displaying the image

            storageRef.downloadUrl.addOnSuccessListener { uri: Uri ->
                // Save the download URL to Firebase Database or Firestore
                val imageUrl = uri.toString()
                imageRef = imageUrl
                val status = StatusModel(
                    "${authInfo!!.firstname.toString()} ${authInfo!!.lastname.toString()}",
                    authInfo!!.userid!!,
                    imageRef,
                    statusTime
                )
                FirebaseFirestore.getInstance().collection("All_Users_Status")
                    .document(References.getCurrentUserId()).set(status)
                    .addOnFailureListener {
                        Toast.makeText(this.context, it.message, Toast.LENGTH_LONG).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(view?.context, " Failed to store image url", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadBitmapToFirebase(bitmap: Bitmap, statusTime: Timestamp) {
        // Convert Bitmap to byte array
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()
        val storageRef = FirebaseStorage.getInstance().reference.child("Status")
            .child(References.getCurrentUserId())
        val uploadTask = storageRef.putBytes(imageData)
        uploadTask.addOnSuccessListener {
            // Image uploaded successfully
            // Now you can retrieve the download URL to use in displaying the image

            storageRef.downloadUrl.addOnSuccessListener { uri: Uri ->
                // Save the download URL to Firebase Database or Firestore
                val imageUrl = uri.toString()
                imageRef = imageUrl
                val status = StatusModel(
                    "${authInfo?.firstname.toString()} ${authInfo?.lastname.toString()}",
                    authInfo?.userid!!,
                    imageRef,
                    statusTime
                )
                fdb.document(References.getCurrentUserId()).set(status)
                    .addOnFailureListener {
                        Toast.makeText(this.context, it.message, Toast.LENGTH_LONG).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(view?.context, " Failed to store image bitmap", Toast.LENGTH_SHORT)
                .show()
        }
    }
}