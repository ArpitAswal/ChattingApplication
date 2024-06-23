package com.example.whatsappclone.contact

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import com.example.whatsappclone.R
import com.example.whatsappclone.country.CountrySelectActivity
import com.example.whatsappclone.firebase.References
import com.example.whatsappclone.model.ContactSaved
import java.util.Random

class NewContactActivity : AppCompatActivity() {

    private lateinit var firstname: EditText
    private lateinit var lastname: EditText
    private lateinit var code: EditText
    private lateinit var phone: EditText

    private lateinit var save: Button

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_contact)
        setSupportActionBar(findViewById(R.id.newcontact_toolbar))

        // Get the ActionBar
        val actionBar = supportActionBar
        // Set custom leading icon
        actionBar?.setHomeAsUpIndicator(R.drawable.leading_black_icon)
        // Enable the home button
        actionBar?.setDisplayHomeAsUpEnabled(true)
        init()

        code.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Check if the touch event is within the drawableEnd bounds
                if (event.rawX >= (code.right - code.compoundDrawables[2].bounds.width())) {
                    // Perform your action here when the drawableEnd is clicked
                    // For example, show a dropdown menu or perform some other action
                    val intent = Intent(this, CountrySelectActivity::class.java)
                    startActivityForResult(intent, 101)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        save.setOnClickListener {
            saveClicked()
        }
    }

    private fun saveClicked() {
        val id = randomReceiverUserId()
        val number = code.text.split("+")[1]
        val contact = ContactSaved(
            id,
            firstname.text.toString(),
            lastname.text.toString(),
            "$number ${phone.text}",
            "", ""
        )
        References.addNewContact(contact){ _ ->
            run {
                // Navigate back to the previous activity
                val intent = Intent(this, ContactActivity::class.java)
                intent.putExtra(
                    "message",
                    "${firstname.text}${lastname.text} was added to your contacts"
                )
                intent.putExtra(
                    "newContactID",
                    id
                )
                startActivity(intent)
                finish()
            }
        }

    }

    private fun randomReceiverUserId(): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = Random()
        return (1..28)
            .map { allowedChars[random.nextInt(allowedChars.length)] }
            .joinToString("")
    }

    private fun init(){
        code = findViewById(R.id.countryCode)
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        // Calculate the desired width based on screen size
        val desiredWidth = (screenWidth * 0.8).toInt() // Adjust the factor as needed

        // Set the width of the view programmatically
        code.width = desiredWidth

        save = findViewById(R.id.saveBtn)
        firstname = findViewById(R.id.firstnameTV)
        lastname = findViewById(R.id.lastnameTV)
        phone = findViewById(R.id.phoneTV)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            val selectedCountryCode = data?.getStringExtra("countryCode")
            val selectedCountryFlag = data?.getStringExtra("countryFlag")
            if (!selectedCountryCode.isNullOrEmpty() && !selectedCountryFlag.isNullOrEmpty()) {
                code.setText("$selectedCountryFlag $selectedCountryCode")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle the leading back arrow click event
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}