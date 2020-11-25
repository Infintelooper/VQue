package com.cleancodec.vque

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Debug
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_landing.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.schedule


private const val TAG:String = "FILESTORE SEARCH LOG"

//for shop select
var selected:Boolean = false
var name:String = ""

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
                    Log.d(TAG, "Error : ${it.exception!!.message}")
                }
            }
        }

        notification_btn.setOnClickListener {
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

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

                if (shop_search_editText.length() > 0) {
                    search_bar_extended.animate()
                        .alpha(1f)
                        .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100

                    search_list.animate()
                        .alpha(1f)
                        .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100
                } else {
                    search_bar_extended.animate()
                        .alpha(0f)
                        .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100

                    search_list.animate()
                        .alpha(0f)
                        .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100
                }
                search_bar_extended.setOnClickListener {
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
                selectText() //code to update shop_search_editText

                selected = false
            }
        })


        //code for add contents
        //generate.setOnClickListener {
          //  showAlertDialog()
        //}
        generate.setOnClickListener {


            //disbale generate button
            generate.alpha = 0f
            generate.isClickable = false
            //loading
            spin_kit.visibility = View.VISIBLE

            Handler().postDelayed({
                Handler().postDelayed({
                    makeSlipVisible()
                }, 300)
                //make loading invisible
                spin_kit.visibility = View.INVISIBLE
            }, 2000)

        }
        authentication() // code for enable auto login
    }

    private fun makeSlipVisible(){


        //token generation code
        tokenslip.animate()
            .alpha(1f)
            .setInterpolator(AccelerateDecelerateInterpolator()).duration = 200
        tokenid.animate()
            .alpha(1f)
            .setInterpolator(AccelerateDecelerateInterpolator()).duration = 200
        token.animate()
            .alpha(1f)
            .setInterpolator(AccelerateDecelerateInterpolator()).duration = 200
        tokentime.animate()
            .alpha(1f)
            .setInterpolator(AccelerateDecelerateInterpolator()).duration = 200
        Toast.makeText(this@landing, "successfully generated", Toast.LENGTH_SHORT).show()

    }
    private fun authentication(){
        //code for keep sign in the app
        val preference=getSharedPreferences(
            resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        val editor=preference.edit()
        editor.putBoolean("isAuthenticated", true)
        editor.apply()

    }

    private fun selectText() {

        // intent code
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,
            IntentFilter("custom-message")
        )
        //code to update
    }
    var mMessageReceiver:BroadcastReceiver = object:BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            val shopName = intent.getStringExtra("data_pass")
            if(!selected)
                hideKeyboard()
            selected = true
            name = shopName.toString()
            updateText(name)

            //code to make generate btn visible
            generate.alpha = 1f
            generate.isClickable = true
            //hideKeyboard()
        }
    }
    fun updateText(name: String){
        Log.i("Hai", name)
        shop_search_editText.setText(name)
        shop_search_editText.clearFocus()
        search_bar_extended.animate()
            .alpha(0f)
            .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100

        search_list.animate()
            .alpha(0f)
            .setInterpolator(AccelerateDecelerateInterpolator()).duration = 100
    }

    fun hideKeyboard() {
        Log.i("Keyboard","Disabled")
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun searchInFirebase(searchText: String) {
        //Search Query
        firebaseFirestore.collection("merchants").whereArrayContains("search_keywords", searchText).limit(
            3
        ).get()
            .addOnCompleteListener{
                if(it.isSuccessful){
                    //get the list and set it to adapter
                    searchList = it.result!!.toObjects(SearchModel::class.java)
                    searchListAdapter.searchList = searchList
                    searchListAdapter.notifyDataSetChanged()
                }else{
                    Log.d(TAG, "Error: ${it.exception!!.message}")
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
         alertDialog.setPositiveButton(
             "Add"
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

        val bookMap = HashMap<String, Any>()
        bookMap["title"] = inputText
        bookMap["search_keywords"] = searchKeywords

        //add to firebase
        firebaseFirestore.collection("merchants").add(bookMap).addOnCompleteListener{
            if(!it.isSuccessful){
                Log.d(TAG, "Error: ${it.exception!!.message}")
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
            inputString = inputString.replace("$word ", "")
        }
        return keywords
    }

    override fun onBackPressed()
    {
                AlertDialog.Builder(this)
                    .setTitle("Exit Alert")
                    .setMessage("Do You Want To Exit VQue App?")
                    .setPositiveButton(android.R.string.ok) { dialog, whichButton ->
                        super.onBackPressed()
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, whichButton ->

                    }
                    .show()

        }

}
