package com.yuni.myportforlio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.yuni.myportforlio.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this,R.layout.activity_login)
        binding.activityLogin = this@LoginActivity

        auth = FirebaseAuth.getInstance()

        supportActionBar?.title = getString(R.string.loginTitle)

    }

    fun loginClick(){
        val emailText = loginMailEditText.text.toString()
        val passText = loginPassEditText.text.toString()

        auth.signInWithEmailAndPassword(emailText, passText)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext, getString(R.string.loginSuccessful),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        baseContext, getString(R.string.loginFailed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    fun signUpClick(){
        startActivity(Intent(this,SignActivity::class.java))
    }
}
