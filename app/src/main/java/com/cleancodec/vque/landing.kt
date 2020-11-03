package com.cleancodec.vque

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_landing.*
import kotlinx.android.synthetic.main.activity_signin.*


class landing : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        //create instance of notification_fragment
        val notification_f = notification()
        //create instance of search fragment
        val result_f = search()

        makeCurrentFragment(notification_f,result_f)

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
    private fun makeCurrentFragment(fragment_n: Fragment,fragment_s: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.notification_panel, fragment_n)
            commit()
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.search_bar_extended, fragment_s)
            commit()
        }
    }
}
