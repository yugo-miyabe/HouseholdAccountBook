package com.yuni.myportforlio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SignActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
    }
/*
    fun registerCliked(){
        val emailText = mailEditText.text.toString()
        val passText = passEditText.text.toString()

        auth.createUserWithEmailAndPassword(emailText, passText)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext, "SignUp 成功",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        baseContext, "SignUp 失敗",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

 */

}