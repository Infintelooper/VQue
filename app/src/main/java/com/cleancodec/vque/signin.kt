package com.cleancodec.vque

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.activity_signin.arrow_back
import kotlinx.android.synthetic.main.activity_signin.editTextCode
import kotlinx.android.synthetic.main.activity_signin.editTextPhone
import kotlinx.android.synthetic.main.activity_signin.login_btn
import kotlinx.android.synthetic.main.activity_signin.progressBarPhone
import kotlinx.android.synthetic.main.activity_signin.sign_up_btn
import kotlinx.android.synthetic.main.activity_signin.textViewTimer
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.concurrent.TimeUnit
import kotlin.math.log


class signin : AppCompatActivity() {


    lateinit var _codeSent:String

    //firebase Authenticator
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        //initilize mAuth
        mAuth = FirebaseAuth.getInstance()


        editTextPhone.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if(editTextPhone.text.toString().length == 10) {
                    progressBarPhone.visibility = View.VISIBLE
                    textViewTimer.visibility = View.VISIBLE
                    // timer code start
                    var count = 61;
                    textViewTimer.text = count.toString()
                    var timer = object: CountDownTimer(60000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            editTextPhone.isEnabled = false
                            sign_up_btn.isEnabled = false
                            count--
                            textViewTimer.text = count.toString()
                        }

                        override fun onFinish() {
                            editTextPhone.isEnabled = true
                            sign_up_btn.isEnabled = true
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

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if(editTextPhone.text.toString().length == 10 && editTextCode.text.toString().length == 6) {

                    verifySignInCode()
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
        login_btn.setOnClickListener() {
            if(editTextPhone.text.toString().length == 10 && editTextCode.text.toString().length == 6) {
                verifySignInCode()
            }
        }

        //for shift to home screen
        arrow_back.setOnClickListener()
        {
            val intent = Intent(this@signin, home::class.java)
            startActivity(intent)
        }
        //for shift to signup screen
        sign_up_btn.setOnClickListener()
        {
            val intent = Intent(this@signin, signup::class.java)
            startActivity(intent)
        }
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
                    Toast.makeText(this@signin, "Login Successfull", Toast.LENGTH_SHORT).show()
                    //start code for move to landing page
                    val intent = Intent(this@signin, landing::class.java)
                    startActivity(intent)
                    //end code
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this@signin, "Incorrect verification code", Toast.LENGTH_SHORT).show()
                        editTextCode.text.clear()
                    }
                }
            }
    }

    private fun sentVerificationCode() {
        var phone:String = editTextPhone.text.toString()
        //---------------
        phone  = "+91$phone"
        Log.i("phone format",phone)
        //-----------------
        if(phone.isEmpty())
        {
            editTextPhone.setError("phone number is required")
            editTextPhone.requestFocus()
            return
        }
        if(phone.length != 13 )
        {
            editTextPhone.setError("please enter a valid phone")
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
            Log.i("Code sent",_codeSent)
        }
    }


}

