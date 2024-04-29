package com.example.whatsappclone.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappclone.R
import com.example.whatsappclone.country.CountrySelectActivity


class SignInActivity : AppCompatActivity() {

    private var countryName = "Choose a country"
    private var countryCode = ""

    private lateinit var countryTextView: EditText
    private lateinit var codeText : TextView
    private lateinit var phoneNumberEditText: EditText
    private lateinit var menu : ImageButton

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        countryTextView = findViewById(R.id.countryTV)
        codeText = findViewById(R.id.phoneNumberCode)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        menu = findViewById(R.id.menuButton)

        findViewById<Button>(R.id.nextButton).setOnClickListener {
            val phoneNumber = phoneNumberEditText.text.toString()
            if (phoneNumber.length < 10) {
                showDialog2()
            } else if (phoneNumber.length > 10) {
                showDialog3()
            } else {
                phoneIntegration()
            }
        }

        // Set a touch listener on the drawableEnd
        countryTextView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Check if the touch event is within the drawableEnd bounds
                if (event.rawX >= (countryTextView.right - countryTextView.compoundDrawables[2].bounds.width())) {
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

        menu.setOnClickListener {
            // Initializing the popup menu and giving the reference as current context
            val popupMenu = PopupMenu(this@SignInActivity, menu)

            // Inflating popup menu from popup_menu.xml file
            popupMenu.menuInflater.inflate(R.menu.menu_screen, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                // Toast message on menu item clicked
                when(menuItem.itemId) {
                    R.id.menu_link_device ->{
                        true
                    }
                    R.id.menu_help ->{
                        true
                    }
                    else -> false
                }
            }
            // Showing the popup menu
            popupMenu.show()
        }

    }

    private fun phoneIntegration() {
        if (TextUtils.isEmpty(phoneNumberEditText.text.toString())) {
            // when mobile number text field is empty
            // displaying a toast message.
            Toast.makeText(
                this@SignInActivity,
                "Please enter a valid phone number.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // if the text field is not empty we are calling our
            // send OTP method for getting OTP from Firebase.
            val phone = "${codeText.text}${phoneNumberEditText.text}"
            showDialog(phone)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_screen, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_link_device -> {
                // Handle Link a device action
                true
            }
            R.id.menu_help -> {
                // Handle Help action
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDialog(phoneNumber: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("You entered the phone number")
            .setMessage("Is this OK, or would you like to edit the number?\n\n$countryCode $phoneNumber")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(this, OTPActivity::class.java).apply {
                    putExtra("phoneNumber", phoneNumber)
                }
                startActivity(intent)
            }
            .setNegativeButton("EDIT") { dialog, _ -> dialog.dismiss() }
            .create()
        alertDialog.show()
    }
    private fun showDialog2() {
        AlertDialog.Builder(this)
            .setTitle("Warning")
            .setMessage("The phone number you entered is too short for the country: $countryName.\nInclude your area code if you have not")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showDialog3() {
        AlertDialog.Builder(this)
            .setTitle("Warning")
            .setMessage("The phone number you entered is too long for the country: $countryName")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            val selectedCountryName = data?.getStringExtra("countryName")
            val selectedCountryCode = data?.getStringExtra("countryCode")
            Log.i("carditem","$selectedCountryName")
            Log.i("carditem","$selectedCountryCode")

            if (!selectedCountryName.isNullOrEmpty() && !selectedCountryCode.isNullOrEmpty()) {
                countryName = selectedCountryName
                countryCode = selectedCountryCode
                countryTextView.setText(countryName)
                codeText.text = countryCode
            }
        }
    }

}
