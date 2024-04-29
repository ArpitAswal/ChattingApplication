package com.example.whatsappclone

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LightingColorFilter
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.example.whatsappclone.auth.SignInActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var imgView: ImageView
    private lateinit var btn : Button
    private lateinit var policyText : TextView
    private lateinit var dropdownLayout: LinearLayout
    private lateinit var languageSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        initialize()
        changeColor()
        val text =
            "Read our Privacy Policy. Tap \"Agree and Continue\" to accept the Terms Of Service."

        // Create a SpannableString with the text
        val spannableString = SpannableString(text)

        // Find the indices of "Terms of Service" and "Privacy Policy"
        val indexOfTerms = text.indexOf("Terms Of Service")
        val indexOfPolicy = text.indexOf("Privacy Policy")

        // Apply color spans only if the substrings are found
        if (indexOfTerms != -1 && indexOfPolicy != -1) {
            // Set color to "Terms of Service"
            spannableString.setSpan(
                ForegroundColorSpan(Color.BLUE),
                indexOfTerms,
                indexOfTerms + "Terms Of Service".length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )

            // Set color to "Privacy Policy"
            spannableString.setSpan(
                ForegroundColorSpan(Color.BLUE),
                indexOfPolicy,
                indexOfPolicy + "Privacy Policy".length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        policyText.text = spannableString
        languageSelect()

        btn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun languageSelect() {
        // Define your list of languages
        val languages = listOf(
            "English",
            "French",
            "Japanese",
            "Korean",
            "Hindi",
            "German",
            "Arabic",
            "Greek",
            "Indonesian",
            "Italian",
            "Urdu"
        )

        // Create an ArrayAdapter using the list of languages and a default spinner layout
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        languageSpinner.adapter = adapter

        // Add the spinner to the dropdownLayout programmatically
        dropdownLayout.addView(languageSpinner)
    }

    private fun initialize() {
        imgView = findViewById(R.id.bgImg)
        btn = findViewById(R.id.continueBtn)
        policyText = findViewById(R.id.policyTV)
        dropdownLayout = findViewById(R.id.dropdownLayout)
        languageSpinner = Spinner(this)
    }

    private fun changeColor() {
        val originalDrawable = resources.getDrawable(R.drawable.bg, null)
        val originalBitmap = (originalDrawable as BitmapDrawable).bitmap

        // Create a mutable copy of the bitmap
        val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Create a Canvas and apply the color filter
        val canvas = Canvas(mutableBitmap)
        val colorFilter: ColorFilter = LightingColorFilter(-0x1, 0x00FF00) // Replace black with green
        originalDrawable.colorFilter = colorFilter
        originalDrawable.setBounds(0, 0, canvas.width, canvas.height)
        originalDrawable.draw(canvas)

        // Set the modified bitmap to an ImageView or wherever you want to display it
        imgView.setImageBitmap(mutableBitmap)
    }

}