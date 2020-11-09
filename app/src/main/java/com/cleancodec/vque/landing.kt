package com.cleancodec.vque

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_landing.*
import kotlinx.android.synthetic.main.activity_signin.*
import java.util.*
import kotlin.collections.HashMap

private const val TAG:String = "FILESTORE SEARCH LOG"

class landing : AppCompatActivity() {

    //firebase for search
    private val firebaseAuth:FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore:FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        notification_btn.setOnClickListener(){
            if(notification_panel.alpha == 0f){
                notification_panel.animate()
                    .alpha(1f)
                    .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100
            }
            else{
                notification_panel.animate()
                    .alpha(0f)
                    .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100
            }
        }
        //code for popup search extended area when text changed
        shop_search_editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

                if(shop_search_editText.length() > 0){
                    search_bar_extended.animate()
                        .alpha(1f)
                        .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100
                }
                else{
                    search_bar_extended.animate()
                        .alpha(0f)
                        .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100
                }
                search_bar_extended.setOnClickListener(){
                    search_bar_extended.animate()
                        .alpha(0f)
                        .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100
                }

                //code to execute search function
                //showAlertDialog();
            }
        })

    }
    private fun showAlertDialog(){
        val alertDialog:AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("Search Shop")
        alertDialog.setMessage("Enter shop name here")

        //add input
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        alertDialog.setView(input)

         // add positive button
         alertDialog.setPositiveButton("Conform Add"
         ) { _, _ ->
         //Get value from  input field
         val inputText:String = input.text.toString()

         //add data to firestore
         addToFirestore(inputText)
         }

        //Show alert dialog
        alertDialog.show()
    }

    private fun addToFirestore(inputText: String) {
        val bookMap = HashMap<String,String>()
        bookMap["title"] = inputText
        bookMap["search_keywords"] = inputText.toLowerCase()

        //add to firebase
        firebaseFirestore.collection("Books").add(bookMap).addOnCompleteListener{
            if(!it.isSuccessful){
                Log.d(TAG,"Error: ${it.exception!!.message}")
            }
        }
    }
}
