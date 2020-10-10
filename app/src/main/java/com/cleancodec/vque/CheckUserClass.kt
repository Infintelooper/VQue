package com.cleancodec.vque

import android.util.Log
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_signin.*

class CheckUserClass(var phone:String) {
    fun isUser():Boolean {
        //firebase setup
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users")
        var exist:Boolean = false
        var check:Boolean = false

        val _enteredNumber = phone
        var _checkUser: Query = myRef.orderByChild("id").equalTo(_enteredNumber)
        _checkUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                exist = (snapshot.exists().toString()).toBoolean()
                check = true
                //var name = snapshot.child(phone).child("name").getValue(String.javaClass)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        while(!check)
        {
            //waste some time
        }
        Log.i(exist.toString(),"state")
        return exist

    }
}