package com.cleancodec.vque

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.set


private const val TAG:String = "FILESTORE SEARCH LOG"

class signup : AppCompatActivity() {

    lateinit var _codeSent:String
    //private val firebaseAuth:FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestores:FirebaseFirestore = FirebaseFirestore.getInstance()

    //firebase Authenticator
    lateinit var mAuth: FirebaseAuth

    //firebase setup
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //disable number and code
        editTextPhone.isEnabled = false
        editTextCode.isEnabled = false

        //for shift to login screen
        login_btn.setOnClickListener()
        {
            val intent = Intent(this@signup, signin::class.java)
            startActivity(intent)
            Animatoo.animateSlideRight(this);
            this.finish()
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
                if (editTextPhone.text.toString().length == 10 && editTextName.text.toString().length >3) {
                    closeKeyBoard()
                   searchInFirebase(editTextPhone.text.toString())

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
                if (editTextPhone.text.toString().length == 10 && editTextCode.text.toString().length == 6 && editTextName.text.toString().length >3) {

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
                if (editTextName.text.toString().length > 3) {
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
                if (editTextPhone.text.toString().length == 10) {
                    //editTextCode.isEnabled = true;
                }
            }
        })
        //setup click listener for signin_btn
        sign_up_btn.setOnClickListener {
            if(editTextPhone.text.toString().length == 10 && editTextCode.text.toString().length == 6 && editTextName.text.toString().length >3 ) {
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
    private fun initiateVerification(){

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
    private  fun searchInFirebase(searchText: String){

        var searchList : List<SearchModel> = ArrayList()
        //Search Query
        firebaseFirestores.collection("users").whereEqualTo("phone",searchText).limit(3).get()
            .addOnCompleteListener {

                if (it.isSuccessful) {
                        searchList = it.result!!.toObjects(SearchModel::class.java)
                         if(searchList.isNotEmpty()) {

                             AlertDialog.Builder(this)
                                 .setTitle("User Already Exist")
                                 .setMessage("Do You Want To Sign in to VQue App ?")
                                 .setPositiveButton(android.R.string.ok) { dialog, whichButton ->

                                     //code to move to sign in page
                                     val intent = Intent(this@signup, signin::class.java)
                                     startActivity(intent)
                                     Animatoo.animateSlideRight(this);
                                     this.finish()
                                 }
                                 .setNegativeButton(android.R.string.cancel) { dialog, whichButton ->
                                     editTextPhone.text.clear()
                                 }
                                 .show()

                         }
                    else{
                             initiateVerification()
                         }
                }
                else {
                    Log.d(TAG, "Error: ${it.exception!!.message}")
                }

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
                    Toast.makeText(this@signup, " Successful", Toast.LENGTH_SHORT).show()
                    //start code for move to landing page
                    val intent = Intent(this@signup, landing::class.java)
                    startActivity(intent)
                    this.finish();
                        //code for add to DB register
                    var phone = editTextPhone.text.toString()
                    var name = editTextName.text.toString()

                    if(false){
                        //for realtime DB
                        var helperClass = UserHelperClass(phone, name, phone)
                        myRef.child(phone).setValue(helperClass)
                    }
                    else{
                        //for firestore DB
                        addToFirestore(phone, name)
                    }

                        //end code
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

    private fun addToFirestore(phone: String, name: String) {


        val bookMap = HashMap<String, Any>()
        bookMap["name"] = name
        bookMap["phone"] = phone

        //add to firebase
        firebaseFirestores.collection("users").add(bookMap).addOnCompleteListener{
            if(!it.isSuccessful){
                Log.d(TAG, "Error: ${it.exception!!.message}")
            }
        }
    }

    private fun sentVerificationCode() {
        var phone:String = editTextPhone.text.toString()
        //---------------
        phone  = "+91$phone"
        Log.i("phone format", phone)
        //-----------------

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
            //return
        }
        if(phone.length != 13 )
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

            editTextCode.isEnabled = true

            Toast.makeText(this@signup, "Code Sent", Toast.LENGTH_SHORT).show()
        }
    }

    //keyboard hide code


    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }







}