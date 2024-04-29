package com.example.whatsappclone.auth

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import com.chaos.view.PinView
import com.example.whatsappclone.HomeActivity
import com.example.whatsappclone.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var scan: ProgressBar
    private lateinit var pinView: PinView
    private lateinit var title: TextView
    private lateinit var verifyTV: TextView
    private lateinit var auth : FirebaseAuth
    private var verificationId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)
        pinView = findViewById(R.id.pinview)
        auth = FirebaseAuth.getInstance()
        toolbar = findViewById(R.id.toolbar)
        scan = findViewById(R.id.scanOTP)
        title = findViewById(R.id.verifyTV)
        verifyTV = findViewById(R.id.sendNumber)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        // showing the back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getOTP()
        pinView.addTextChangedListener {
            if(pinView.text?.length==6){
                val otp = pinView.text.toString()
                scan.visibility = View.VISIBLE
                verifyCode(otp)
            }
        }

    }

    private fun sendVerificationCode(number: String) {
            // this method is used for getting
            // OTP on user phone number.
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(number) // Phone number to verify
                .setTimeout(10L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this) // Activity (for callback binding)
                .setCallbacks(mCallBack) // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }

        // callback method is called on Phone auth provider.
        // initializing our callbacks for on verification callback method.
        private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                // below method is used when
                // OTP is sent from Firebase
                override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(s, forceResendingToken)
                    // when we receive the OTP it
                    // contains a unique id which
                    // we are storing in our string
                    // which we have already created.
                    verificationId = s
                }

                // this method is called when user
                // receive OTP from Firebase.
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    // below line is used for getting OTP code
                    // which is sent in phone auth credentials.
                    val code = phoneAuthCredential.smsCode

                    // checking if the code
                    // is null or not.
                    if (code != null) {
                        // if the code is not null then
                        // we are setting that code to
                        // our OTP edittext field.
                        pinView.setText(code)

                        // after setting this code
                        // to OTP edittext field we
                        // are calling our verifycode method.
                         verifyCode(code)
                    }
                }

                // this method is called when firebase doesn't
                // sends our OTP code due to any error or issue.
                override fun onVerificationFailed(e: FirebaseException) {
                    // displaying error message with firebase exception.
                    Toast.makeText(this@OTPActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }




    private fun getOTP() {
        val intent = intent
        val number = intent.getStringExtra("phoneNumber")!!
        title.text = "Verify $number"
        verifyTV.text = "$number"
        sendVerificationCode(number)
    }


    // below method is use to verify code from Firebase.
    private fun verifyCode(code: String) {
        // below line is used for getting
        // credentials from our verification id and code.
        val credential = PhoneAuthProvider.getCredential(verificationId, code)

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    scan.visibility = View.GONE
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()

                } else {
                    scan.visibility = View.GONE
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    fun resendOTP(view: View) {
        getOTP()
    }
}