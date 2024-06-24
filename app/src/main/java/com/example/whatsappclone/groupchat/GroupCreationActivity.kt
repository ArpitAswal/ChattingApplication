package com.example.whatsappclone.groupchat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappclone.R
import com.example.whatsappclone.adapter.GridAdapter
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.model.ContactSaved
import com.example.whatsappclone.model.GroupModel
import com.example.whatsappclone.model.MessagesModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GroupCreationActivity : AppCompatActivity() {

    private val capture = 1
    private val pick = 2
    private lateinit var authUser: ContactSaved
    private lateinit var rdb: DatabaseReference
    private lateinit var fdb: CollectionReference
    private lateinit var camera: FrameLayout
    private lateinit var gallery: FrameLayout
    private lateinit var groupProfile: ImageView
    private lateinit var groupnameEdit: EditText
    private lateinit var bottomSheetDialog : BottomSheetDialog
    private lateinit var bottomSheetView : View
    private lateinit var membersCount: TextView
    private lateinit var done: FloatingActionButton
    private lateinit var firstMember : ContactSaved
    private var membersList = ArrayList<ContactSaved>()
    private var groupId = ""
    private var imageRef = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_creation)
        CoroutineScope(Dispatchers.Main).launch {
            // Call your suspend function within the coroutine
            authUser = References.getCurrentAuthUserInfo()!!
        }
        init()
        groupProfile.setOnClickListener {
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
        done.setOnClickListener {
            val currentTime = Calendar.getInstance().time // Current time as Date object
            val dateFormat = SimpleDateFormat("dd MMMM yyyy 'at' HH:mm:ss 'UTC'XXX", Locale.getDefault())
            val timestamp = dateFormat.format(currentTime)
            membersList.add(0,firstMember)
            val groupData = GroupModel(
                groupId, membersList, groupnameEdit.text.toString(), imageRef
            )
            val model = MessagesModel(
                "${authUser.firstname.toString()} ${authUser.lastname.toString()}",
                authUser.userid!!,
                "",
                timestamp
            )
            rdb.child(groupId).push().setValue(model)
            fdb.document(groupId).set(groupData)
            val nextIntent = Intent(this, GroupChatDetailActivity::class.java)
            nextIntent.putExtra("SelectedMembers", membersList)
            nextIntent.putExtra("GroupName", groupnameEdit.text.toString())
            nextIntent.putExtra("GroupImage",imageRef)
            nextIntent.putExtra("GroupId",groupId)

            startActivity(nextIntent)
            finish()
        }
    }

    private fun init(){
        rdb = References.getGroupRef()
        fdb = References.getAllGroupsInfo()
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet, null)
        gallery = bottomSheetView.findViewById(R.id.gallery_layout)
        camera = bottomSheetView.findViewById(R.id.camera_layout)
        groupProfile = findViewById(R.id.groupProfile)
        groupnameEdit = findViewById(R.id.group_name_editText)
        membersCount = findViewById(R.id.membersText)
        done = findViewById(R.id.memberFAB)
        val intent = intent
        membersList = (intent.getSerializableExtra("SelectedMembers") as? ArrayList<ContactSaved>)!!
        for(ids in membersList){
            groupId += ids.userid.toString()
        }
        val gridView: GridView = findViewById(R.id.gridView)
        val adapter = GridAdapter(this, membersList) // Assume you have a list of users
        gridView.adapter = adapter
        firstMember = membersList.removeAt(0)
        membersCount.text = "Members: ${membersList.size}"
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, capture)
            }
        }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickPhotoIntent ->
            pickPhotoIntent.type = "image/*"
            startActivityForResult(pickPhotoIntent, pick)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                capture -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    groupProfile.setImageBitmap(imageBitmap)
                    // Do something with the captured image bitmap
                    uploadBitmapToFirebase(imageBitmap)
                }
                pick -> {
                    val selectedImageUri = data?.data
                    if(selectedImageUri!=null) {
                        groupProfile.setImageURI(selectedImageUri)
                        uploadImageToFirebase(selectedImageUri)
                    }
                }
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference.child("Group").child(groupId)
        val uploadTask = storageRef.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            // Image uploaded successfully
            // Now you can retrieve the download URL to use in displaying the image
            storageRef.downloadUrl
                .addOnSuccessListener { uri: Uri ->
                    // Save the download URL to Firebase Database or Firestore
                    val imageUrl = uri.toString()
                    imageRef = imageUrl
                }
        }.addOnFailureListener { }
    }

    private fun uploadBitmapToFirebase(bitmap: Bitmap) {
        // Convert Bitmap to byte array
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()
        val storageRef = FirebaseStorage.getInstance().reference.child("Group").child(groupId)
        val uploadTask = storageRef.putBytes(imageData)
        uploadTask.addOnSuccessListener {
            // Image uploaded successfully
            // Now you can retrieve the download URL to use in displaying the image
            storageRef.downloadUrl
                .addOnSuccessListener { uri: Uri ->
                    // Save the download URL to Firebase Database or Firestore
                    val imageUrl = uri.toString()
                    imageRef = imageUrl
                }
        }.addOnFailureListener { }

    }

}