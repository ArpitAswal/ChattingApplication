package com.example.whatsappclone

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.model.ContactSaved
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class SetProfileActivity : AppCompatActivity() {

    private lateinit var image: CircleImageView
    private lateinit var navigate: Button
    private lateinit var camera: ImageButton
    private lateinit var firstname: TextInputLayout
    private lateinit var lastname: TextInputLayout
    private lateinit var fdb: CollectionReference
    private lateinit var fdb3: CollectionReference
    private lateinit var loading: ProgressBar
    private var imageURI : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)
        init()
        val previousIntent = intent
        camera.setOnClickListener {
            val cameraIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(cameraIntent,101)
        }

        navigate.setOnClickListener {
            if(!TextUtils.isEmpty(firstname.editText?.text)){
                loading.visibility = View.VISIBLE
                val username = "${firstname.editText?.text}${lastname.editText?.text}"
                val number = previousIntent.getStringExtra("phoneNumber")
                val code = number?.subSequence(0,3)
                val phone = number?.subSequence(3,number.length)
                var img = ""
                if(imageURI!=null) {
                    val storageRef = FirebaseStorage.getInstance().reference.child(References.getCurrentUserId()).child("profile_images")
                    storageRef.putFile(imageURI!!).addOnSuccessListener {
                        val result = storageRef.downloadUrl
                        result.addOnSuccessListener {
                            img = it.toString()
                            contactSaved(username, code, phone, img)
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Image does not Stored the reason is: ${it.message.toString()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        loading.visibility = View.GONE
                    }
                }
                else{
                    contactSaved(username, code, phone, img)
                }
            }
        }
    }

    private fun contactSaved(username: String, code: CharSequence?, phone: CharSequence?, img: String) {
        val userSignIn = ContactSaved(
            References.getCurrentUserId(),
            firstname.editText?.text.toString(),
            lastname.editText?.text.toString(),
            "$code $phone",
            img,
            ""
        )
        fdb.document(username).set(userSignIn).addOnSuccessListener {
            fdb3.document(userSignIn.userid!!).set(userSignIn).addOnSuccessListener {
                loading.visibility = View.GONE
                val intent = Intent(this@SetProfileActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                loading.visibility = View.GONE
                Log.i("failure exception:", "${it.message.toString()}")
                Toast.makeText(this, "Failed to store user data", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            loading.visibility = View.GONE
            Toast.makeText(this, "Failed to store user data", Toast.LENGTH_SHORT).show()
            Log.i("failure exception:", "${it.message.toString()}")
        }
    }

    private fun init(){
        image = findViewById(R.id.image_view)
        camera = findViewById(R.id.cameraBtn)
        navigate = findViewById(R.id.continueBtn)
        firstname = findViewById(R.id.firstname)
        lastname = findViewById(R.id.lastname)
        loading = findViewById(R.id.saving)
        fdb = References.getAllSignInUsers()
        fdb3 = References.getAllContactsInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101 && resultCode == RESULT_OK && data!=null){
            imageURI = data.data!!
            image.setImageURI(imageURI)
        }
    }
}