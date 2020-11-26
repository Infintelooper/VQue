package com.cleancodec.vque

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import kotlinx.android.synthetic.main.activity_home.*



class home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //for shift to login screen
        login_btn.setOnClickListener()
        {


            val intent = Intent(this@home, signin::class.java)
            startActivity(intent)
            Animatoo.animateSlideLeft(this);
            finish()
        }
         //for shift to signup screen
        sign_up_btn.setOnClickListener()
        {
            val intent = Intent(this@home, signup::class.java)
            startActivity(intent)
            Animatoo.animateSlideLeft(this);
            this.finish();
        }
    }
}