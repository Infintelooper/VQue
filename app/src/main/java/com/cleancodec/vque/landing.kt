package com.cleancodec.vque

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_landing.*
import java.util.*
import kotlin.collections.HashMap

private const val TAG:String = "FILESTORE SEARCH LOG"

class landing : AppCompatActivity() {

    //firebase for search
    private val firebaseAuth:FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore:FirebaseFirestore = FirebaseFirestore.getInstance()

    //recycle view
    private var searchList : List<SearchModel> = ArrayList()
    private val searchListAdapter = SearchListAdapter(searchList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        //check if user login in, require for accessing database
        if(firebaseAuth.currentUser == null){
            //create new user
            firebaseAuth.signInAnonymously().addOnCompleteListener{
                if(!it.isSuccessful){
                    Log.d(TAG,"Error : ${it.exception!!.message}")
                }
            }
        }

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

        //Setup recycler view
        search_list.hasFixedSize()
        search_list.layoutManager = LinearLayoutManager(this)
        search_list.adapter = searchListAdapter


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

                    search_list.animate()
                        .alpha(1f)
                        .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100
                }
                else{
                    search_bar_extended.animate()
                        .alpha(0f)
                        .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100

                    search_list.animate()
                        .alpha(0f)
                        .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100
                }
                search_bar_extended.setOnClickListener(){
                    search_bar_extended.animate()
                        .alpha(0f)
                        .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100

                    search_list.animate()
                        .alpha(0f)
                        .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100
                }

                //code to execute search function
                //showAlertDialog();

                //get value of field
                val searchText: String = shop_search_editText.text.toString()

                //search in firestore
                searchInFirebase(searchText.toLowerCase())
            }
        })


        //code for add contents
        button.setOnClickListener(){
            showAlertDialog();
        }
    }

    private fun searchInFirebase(searchText: String) {
        //Search Query
        firebaseFirestore.collection("merchants").whereArrayContains("search_keywords",searchText).limit(3).get()
            .addOnCompleteListener{
                if(it.isSuccessful){
                    //get the list and set it to adapter
                    searchList = it.result!!.toObjects(SearchModel::class.java)
                    searchListAdapter.searchList = searchList
                    searchListAdapter.notifyDataSetChanged()
                }else{
                    Log.d(TAG,"Error: ${it.exception!!.message}")
                }
            }
    }

    private fun showAlertDialog(){
        val alertDialog:AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("Add Merchant")
        alertDialog.setMessage("Enter shop name here")

        //add input
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        alertDialog.setView(input)

         // add positive button
         alertDialog.setPositiveButton("Add"
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

        //keywords
        val searchKeywords = generateSearchkeywords(inputText)

        val bookMap = HashMap<String,Any>()
        bookMap["title"] = inputText
        bookMap["search_keywords"] = searchKeywords

        //add to firebase
        firebaseFirestore.collection("merchants").add(bookMap).addOnCompleteListener{
            if(!it.isSuccessful){
                Log.d(TAG,"Error: ${it.exception!!.message}")
            }
        }
    }

    private fun generateSearchkeywords(inputText: String) : MutableList<String> {
        var inputString = inputText.toLowerCase()
        var keywords = mutableListOf<String>()

        //split all words from the string
        val words = inputString.split(" ")

        //for each word
        for(word in words){
            var appendString = ""

            //for each character in the whole string
            for (charPosition in inputString.indices){
                appendString += inputString[charPosition].toString()
                keywords.add(appendString)
            }

            //remove first word form the string
            inputString = inputString.replace("$word ","")
        }
        return keywords
    }
}
