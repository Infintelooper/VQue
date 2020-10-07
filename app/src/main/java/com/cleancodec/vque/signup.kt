package com.cleancodec.vque

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_signin.*

class signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

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
    }
}