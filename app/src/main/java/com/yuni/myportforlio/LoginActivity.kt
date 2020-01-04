package com.yuni.myportforlio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
/*
        val binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this,R.layout.activity_login)
        binding.activityLogin = this@LoginActivity

 */


    }
/*
    fun loginCliked(){

    }

    fun signUpCliked(){
        startActivity(Intent(this,SignActivity::class.java))
    }

 */
}
