package com.cleancodec.vque

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.activity_signup.arrow_back
import kotlinx.android.synthetic.main.activity_signup.editTextCode
import kotlinx.android.synthetic.main.activity_signup.editTextPhone
import kotlinx.android.synthetic.main.activity_signup.login_btn
import kotlinx.android.synthetic.main.activity_signup.progressBarPhone
import kotlinx.android.synthetic.main.activity_signup.textViewTimer
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.sign_up_btn
import java.util.concurrent.TimeUnit

class signup : AppCompatActivity() {

    lateinit var _codeSent:String

    //firebase Authenticator
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //disable number and code
        editTextPhone.isEnabled = false
        editTextCode.isEnabled = false

        //for shift to home screen
        arrow_back.setOnClickListener()
        {
            val intent = Intent(this@signup, home::class.java)
            startActivity(intent)
        }
        //for shift to login screen
        login_btn.setOnClickListener()
        {
            val intent = Intent(this@signup, signin::class.java)
            startActivity(intent)
        }

        //initilize mAuth
        mAuth = FirebaseAuth.getInstance()


        editTextPhone.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (editTextPhone.text.toString().length == 10 && editTextName.text.toString().length >= 3) {
                    //closeKeyBoard() // close keyboard
                    progressBarPhone.visibility = View.VISIBLE
                    textViewTimer.visibility = View.VISIBLE
                    // timer code start
                    var count = 61
                    textViewTimer.text = count.toString()
                    var timer = object : CountDownTimer(60000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            editTextPhone.isEnabled = false
                            login_btn.isEnabled = false
                            count--
                            textViewTimer.text = count.toString()
                        }

                        override fun onFinish() {
                            editTextPhone.isEnabled = true
                            login_btn.isEnabled = true
                            progressBarPhone.visibility = View.INVISIBLE
                            textViewTimer.visibility = View.INVISIBLE
                        }
                    }
                    timer.start()
                    //timer code end
                    sentVerificationCode()
                }
            }
        })
        editTextCode.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (editTextPhone.text.toString().length == 10 && editTextCode.text.toString().length == 6 && editTextName.text.toString().length >=3 ) {

                    verifySignInCode()
                }
            }
        })
        editTextName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(editTextName.text.toString().length >=3) {
                    editTextPhone.isEnabled = true;
                }
            }
        })
        editTextPhone.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(editTextPhone.text.toString().length == 10) {
                    editTextCode.isEnabled = true;
                }
            }
        })
        //setup click listener for signin_btn
        sign_up_btn.setOnClickListener {
            if(editTextPhone.text.toString().length == 10 && editTextCode.text.toString().length == 6 && editTextName.text.toString().length >=3 ) {
                verifySignInCode()
            }
        }
        //-------------------
        editTextName.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                closeKeyBoard()
            }
        }

        //-------------------

    }

    private fun verifySignInCode() {
        var code = editTextCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(_codeSent, code)
        signInWithPhoneAuthCredential(credential)
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    //on login successfull activity
                    Toast.makeText(this@signup, "Login Successfull", Toast.LENGTH_SHORT).show()
                    //start code for move to landing page
                    val intent = Intent(this@signup, landing::class.java)
                    startActivity(intent)
                    //end code
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            this@signup,
                            "Incorrect verification code",
                            Toast.LENGTH_SHORT
                        ).show()
                        editTextCode.text.clear()
                    }
                }
            }
    }

    private fun sentVerificationCode() {
        var phone:String = editTextPhone.text.toString()

        if(phone.isEmpty())
        {
            editTextPhone.error = "phone number is required"
            editTextPhone.requestFocus()
            return
        }
        if(editTextName.text.toString().isEmpty())
        {
            editTextName.error = "name is required"
            editTextName.requestFocus()
            return
        }
        if(editTextName.text.toString().length <=3)
        {
            editTextName.error = "please enter full name"
            editTextName.requestFocus()
            return
        }
        if(phone.length != 10 )
        {
            editTextPhone.error = "please enter a valid phone"
            editTextPhone.requestFocus()
            return
        }

        //firebase code
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks
        ) // OnVerificationStateChangedCallbacks

    }

    // Initialize phone auth callbacks
    // [START phone_auth_callbacks]
    var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        }

        override fun onVerificationFailed(e: FirebaseException) {
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(verificationId, token)
            _codeSent = verificationId
        }
    }

    //keyboard hide code


    private fun closeKeyBoard() {
        val inputManager:InputMethodManager =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED)
    }







}