package com.cleancodec.vque

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*



class home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //for shift to login screen
        login_btn.setOnClickListener()
        {
            val intent = Intent(this@home, signin::class.java)
            startActivity(intent)
        }
         //for shift to signup screen
        sign_up_btn.setOnClickListener()
        {
            val intent = Intent(this@home, signup::class.java)
            startActivity(intent)
        }
    }
}