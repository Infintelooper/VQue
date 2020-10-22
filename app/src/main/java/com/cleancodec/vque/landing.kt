package com.cleancodec.vque

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_landing.*
import kotlinx.android.synthetic.main.activity_signin.*


class landing : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        notification_btn.setOnClickListener(){

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



            }
        })


        }
    }
