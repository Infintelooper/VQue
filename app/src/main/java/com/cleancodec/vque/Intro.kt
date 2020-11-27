package com.cleancodec.vque

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import kotlinx.android.synthetic.main.activity_intro.*

class Intro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        spin_kit_main.animate()
            .alpha(1f)
            .setInterpolator(AccelerateDecelerateInterpolator()).duration = 400
        Handler().postDelayed({
            isAuthenticated()
        }, 5000)
    }
    private fun isAuthenticated(){

        //check authentication
        val preference=getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)
        if(!preference.getBoolean("isAuthenticated",true))
        {
            val intent = Intent(this, landing::class.java)
            startActivity(intent)
            Animatoo.animateInAndOut(this)
            this.finish()

        }
        else
        {
            val intent = Intent(this, home::class.java)
            startActivity(intent)
            Animatoo.animateFade(this)
            this.finish()

        }
    }
}