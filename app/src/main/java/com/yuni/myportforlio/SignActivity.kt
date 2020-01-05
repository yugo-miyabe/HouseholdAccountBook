package com.yuni.myportforlio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.yuni.myportforlio.databinding.ActivitySignBinding

class SignActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        val binding = DataBindingUtil.setContentView<ActivitySignBinding>(this@SignActivity,R.layout.activity_sign)
        binding.activitySign = this@SignActivity

        auth = FirebaseAuth.getInstance()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.singTitle)

    }

    fun registerClick(){
        val emailEditText = findViewById<EditText>(R.id.signMailEditText)
        val passEditText = findViewById<EditText>(R.id.singPassEditText)

        val emailText = emailEditText.text.toString()
        val passText = passEditText.text.toString()

        auth.createUserWithEmailAndPassword(emailText, passText)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext, getString(R.string.singSuccessful),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        baseContext, getString(R.string.singFailed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }



}