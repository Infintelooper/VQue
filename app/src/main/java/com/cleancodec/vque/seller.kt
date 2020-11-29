package com.cleancodec.vque

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.blogspot.atifsoftwares.animatoolib.Animatoo

class seller : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller)
    }

    override fun onBackPressed()
    {
        AlertDialog.Builder(this)
            .setTitle("Back to home page")
            .setMessage("Are you sure to exit seller page ?")
            .setPositiveButton(android.R.string.ok) { dialog, whichButton ->
                val intent = Intent(this, landing::class.java)
                startActivity(intent)
                Animatoo.animateSlideRight(this)
                this.finish()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, whichButton ->

            }
            .show()

    }
}