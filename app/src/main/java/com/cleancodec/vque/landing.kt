

Skip to content
Using Gmail with screen readers
Meet
New meeting
Join a meeting
Hangouts

1 of 734
none
Inbox

Akhil Reji <akhil.reji141@gmail.com>
Attachments
12:58 (0 minutes ago)
to me


Attachments area

package com.cleancodec.vque

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_landing.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


private const val TAG:String = "FILESTORE SEARCH LOG"

//for shop select
var selected:Boolean = false
var name:String = ""

class landing : AppCompatActivity() {

    //toolbar
    lateinit var xToolbar: Toolbar

    //firebase for search
    private val firebaseAuth:FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore:FirebaseFirestore = FirebaseFirestore.getInstance()

    //recycle view
    private var searchList : List<SearchModel> = ArrayList()
    private val searchListAdapter = SearchListAdapter(searchList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        xToolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(xToolbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false) //used to disable app name from toolbar
        xToolbar.setTitle("")
        xToolbar.setSubtitle("")

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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

            generateToken()
            //disbale generate button
            generate.alpha = 0f
            generate.isClickable = false
            //loading
            spin_kit.visibility = View.VISIBLE

            Handler().postDelayed({

                //make loading invisible
                spin_kit.visibility = View.INVISIBLE
            }, 4300)
            Handler().postDelayed({
                makeSlipVisible()
            },4500)

        }
        authentication() // code for enable auto login

        //code for click events in toolbar menu items
        pin.setOnClickListener{
            if(pin.visibility == View.VISIBLE){
                val preference = getSharedPreferences(
                    resources.getString(R.string.app_name),
                    Context.MODE_PRIVATE
                )
                if (preference.getBoolean("pinned", true)) {
                    //code to remove pin
                    unPin()
                }
                else{
                    //code to make pin
                    pin()
                }
            }
        }
    }

    private fun generateToken(){
        //create a new collection in firestore with name ' token'  and contains ' id,token,date' + 'shop no,user no. shop no and user no not should be same
        addToFirestoreToken()
        //token should be incremented with 1 for same shop
        //store it on local storage
        //pin token
    }
    private fun deleteToken(){
        //remove token from local storage
        //unpin token
    }
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun addToFirestoreToken(){

        val preference = getSharedPreferences(
            resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        var id:String = "0"
        var tokens:String = "0"
        //retrieve date
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        var date:String = sdf.format(Date()).toString()
        //retrieve from local storage
        var user:String = preference.getString("phone", "0000000000").toString()
        //retrieve from firestore
        var shop:String = "0000000000"
        firebaseFirestore.collection("merchants").whereEqualTo(
            "title",
            shop_search_editText.text.toString()
        ).limit(
            1
        ).get()
            .addOnCompleteListener{

                //
                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        shop = document.getString("phone").toString()
                        Handler().postDelayed({
                            Handler().postDelayed({

                                if (shop!= user) {
                                    //code to proceed
                                    tokentime.text = date
                                    id = (shop.takeLast(4))+(user.takeLast(4))
                                    tokenid.text = id


                                    //token number generation
                                    var maxToken:Int = 1
                                    firebaseFirestore.collection("token").whereEqualTo(
                                        "shop",
                                        shop
                                    ).get()

                                        .addOnCompleteListener{
                                            if (it.isSuccessful) {
                                                for (document in it.result!!) {
                                                    if (document.getString("date").toString() == date ){


                                                        Log.i("token :",document.getString("token").toString())
                                                        if(maxToken <= document.getString("token")!!.toInt()) {


                                                            maxToken =
                                                                document.getString("token")!!
                                                                    .toInt() + 1
                                                            tokens =
                                                                (document.getString("token")!!
                                                                    .toInt() + 1).toString()
                                                        }

                                                    }
                                                }
                                                if(maxToken < 10)
                                                    token.text = "00$maxToken"
                                                else if(maxToken<100)
                                                    token.text = "0$maxToken"
                                                else
                                                    token.text = maxToken.toString()

                                                tokens = maxToken.toString()

                                                Handler().postDelayed({
                                                    //code to add to firestore
                                                    val bookMap = HashMap<String, Any>()
                                                    bookMap["id"] = id
                                                    bookMap["token"] = tokens
                                                    bookMap["date"] = date
                                                    bookMap["shop"] = shop
                                                    bookMap["user"] = user

                                                    //add to firebase
                                                    firebaseFirestore.collection("token")
                                                        .add(bookMap).addOnCompleteListener {
                                                            if (!it.isSuccessful) {
                                                                Log.d(
                                                                    TAG,
                                                                    "Error: ${it.exception!!.message}"
                                                                )
                                                            }
                                                        }
                                                    //end of code
                                                },2000)
                                            }

                                        }
                                }
                            }, 300)
                            //make loading invisible
                            spin_kit.visibility = View.INVISIBLE
                        }, 3000)
                    }
                }

            }




    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.help -> {
                Toast.makeText(this@landing, "Help", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.logout -> {
                AlertDialog.Builder(this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure to logout ?")
                    .setPositiveButton(android.R.string.yes) { dialog, whichButton ->
                        //code for change login status in local storage
                        notauthentication()
                        super.onBackPressed()

                        //clear local storage
                        val preferences: SharedPreferences =
                            getSharedPreferences(R.string.app_name.toString(), MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = preferences.edit()
                        editor.clear()
                        editor.apply()
                        finish()
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, whichButton ->

                    }
                    .show()
                true
            }
            R.id.seller -> {
                val preference = getSharedPreferences(
                    resources.getString(R.string.app_name),
                    Context.MODE_PRIVATE
                )
                if (preference.getBoolean("applied", true)) {
                    //code for check firestore
                    if (false) {
                        //code for move to seller page
                    } else {
                        val preference = getSharedPreferences(
                            resources.getString(R.string.app_name),
                            Context.MODE_PRIVATE
                        )
                        Log.i(
                            "Expected Shop name", preference.getString(
                                "shop_name",
                                "defaultStringIfNothingFound"
                            ).toString()
                        )
                        //code for check pending status from firestore
                        val preferences = getSharedPreferences(
                            resources.getString(R.string.app_name),
                            Context.MODE_PRIVATE
                        )
                        var boolean: Boolean = false
                        var boolean2: Boolean = false
                        var shop: String = preferences.getString("shop_name", "sample").toString()
                        Log.i("Shop name", shop)
                        if (shop != "sample") {
                            firebaseFirestore.collection("merchants").whereEqualTo("title", shop)
                                .orderBy(
                                    "title"
                                ).limit(
                                    2
                                ).get()
                                .addOnCompleteListener {

                                    //
                                    if (it.isSuccessful) {
                                        for (document in it.result!!) {
                                            if (document.getString("status").toString() == "accept")
                                                boolean = true
                                            if (document.getString("status")
                                                    .toString() == "pending"
                                            )
                                                boolean = false
                                            if (document.getString("status").toString() == "none")
                                                boolean2 = true
                                        }
                                    }

                                }
                                .addOnFailureListener { exception ->
                                    Log.d("TAG", "Error getting documents: ", exception)
                                }

                            Handler().postDelayed(
                                {

                                    if (boolean) {
                                        //code to move to seller page
                                        val intent = Intent(this, seller::class.java)
                                        startActivity(intent)
                                        Animatoo.animateSlideLeft(this)
                                        this.finish()
                                    } else if (boolean2) {
                                        showAlertDialog()
                                    } else {
                                        pending()
                                    }
                                },
                                3000 // value in milliseconds
                            )

                        } else {
                            showAlertDialog()
                        }
                    }
                }else {
                    showAlertDialog()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun pin(){
        Toast.makeText(this@landing, "Pinned", Toast.LENGTH_SHORT).show()

        //change image
        pin.setImageResource(R.drawable.ic_round_push_pin_24)

        val preferences=getSharedPreferences(
            resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        val editor=preferences.edit()
        editor.putBoolean("pinned", true)
        editor.apply()
    }
    private fun unPin(){
        Toast.makeText(this@landing, "Unpinned", Toast.LENGTH_SHORT).show()

        //change image
        pin.setImageResource(R.drawable.ic_outline_push_pin_24)

        val preferences=getSharedPreferences(
            resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        val editor=preferences.edit()
        editor.putBoolean("pinned", false)
        editor.apply()
    }
    //for toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
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
        pin.animate()
            .alpha(1f)
            .setInterpolator(AccelerateDecelerateInterpolator()).duration = 200
        Toast.makeText(this@landing, "successfully generated", Toast.LENGTH_SHORT).show()

    }
    private fun authentication(){

        //code to check pin status

        val preferencess = getSharedPreferences(
            resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        if (preferencess.getBoolean("pinned", true)){

            //make slip visible
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
            pin.animate()
                .alpha(1f)
                .setInterpolator(AccelerateDecelerateInterpolator()).duration = 200

            pin()
        }
        else{
            unPin()
        }

        //code for keep sign in the app
        val preferences=getSharedPreferences(
            resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        val editor=preferences.edit()
        editor.putBoolean("isAuthenticated", true)
        editor.apply()

        //code to update seller applied status with firestore
        val preference=getSharedPreferences(
            resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        var boolean:Boolean = false
        var phone:String = preference.getString("phone", "1111111111").toString()
        firebaseFirestore.collection("users").whereEqualTo("phone", phone).whereNotEqualTo(
            "shop",
            "none"
        ).limit(
            1
        ).get()
            .addOnCompleteListener{
                boolean = true
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
        Log.i("Info", "Im done")

        Handler().postDelayed(
            {
                if (boolean) {
                    sellerApplied()
                } else {
                    sellerNotApplied()
                }
                // This method will be executed once the timer is over
            },
            5000 // value in milliseconds
        )




    }
    private fun sellerNotApplied(){
        //code for cancel application to seller account
        val preference=getSharedPreferences(
            resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        val editor=preference.edit()
        editor.putBoolean("applied", false)
        editor.apply()
    }

    private fun sellerApplied(){

        //code for application to seller account in locally
        val preference=getSharedPreferences(
            resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        val editor=preference.edit()
        editor.putBoolean("applied", true)
        editor.apply()

    }
    private fun changeShopinFirestore(phone: String, shop: String){

        val complaintsRef: CollectionReference = firebaseFirestore.collection("users")
        complaintsRef.whereEqualTo("phone", phone).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        firebaseFirestore.collection("users").document(document.id).update(
                            "shop",
                            shop
                        )
                    }
                }
            }
    }

    private fun notauthentication(){
        //code for keep sign in the app
        val preference=getSharedPreferences(
            resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        val editor=preference.edit()
        editor.putBoolean("isAuthenticated", false)
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
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun searchInFirebase(searchText: String) {
        //Search Query
        firebaseFirestore.collection("merchants").whereArrayContains("search_keywords", searchText).whereEqualTo(
            "status",
            "accepted"
        ).limit(
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
        alertDialog.setTitle("Seller Account")
        alertDialog.setMessage("Enter shop name here")

        //add input
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        alertDialog.setView(input)

        //for to add to local storage
        val preference=getSharedPreferences(
            resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        val editor=preference.edit()
        editor.putString("shop_name", input.toString().toLowerCase())
        editor.apply()

        // add positive button
        alertDialog.setPositiveButton(
            "Apply"
        ) { _, _ ->
            //Get value from  input field
            val inputText:String = input.text.toString()

            //code for retrive phone from sharedpreferences
            val preference=getSharedPreferences(
                resources.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
            var phone:String = preference.getString("phone", "0000000000").toString()

            //update firestore
            changeShopinFirestore(phone, inputText)
            //add data to firestore
            addToFirestore(inputText)
        }

        //Show alert dialog
        alertDialog.show()
    }

    private fun addToFirestore(inputText: String) {

        //get phone from local storage
        val preference = getSharedPreferences(
            resources.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        var phone:String = preference.getString("phone", "0000000000").toString()
        //keywords
        val searchKeywords = generateSearchkeywords(inputText)

        val bookMap = HashMap<String, Any>()
        bookMap["title"] = inputText
        bookMap["status"] = "pending"
        bookMap["phone"] = phone
        bookMap["search_keywords"] = searchKeywords

        //add to firebase
        firebaseFirestore.collection("merchants").add(bookMap).addOnCompleteListener{
            if(!it.isSuccessful){
                Log.d(TAG, "Error: ${it.exception!!.message}")
            }
            else{
                sellerApplied()
                pending()
            }
        }
    }
    private fun pending(){
        AlertDialog.Builder(this)
            .setTitle("Status")
            .setMessage(
                "Pending..."
            )
            .setPositiveButton(android.R.string.ok) { dialog, whichButton ->
                //showAlertDialog()
            }

            .show()
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
h.txt
Displaying h.txt.