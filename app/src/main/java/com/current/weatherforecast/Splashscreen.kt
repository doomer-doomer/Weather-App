package com.current.weatherforecast

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.splashscreen.*


class Splashscreen:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)
        //startwal.setBackgroundResource(R.drawable.daystart)


        Handler().postDelayed(Runnable { // This method will be executed once the wind_dirr is over
            val i = Intent(this@Splashscreen, MainActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            finish()
        }, 2000)
    }
}
