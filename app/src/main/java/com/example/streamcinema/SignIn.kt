package com.example.streamcinema

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.streamcinema.databinding.ActivitySignInBinding
import com.example.streamcinema.model.EmailPass
import kotlinx.coroutines.launch

class SignIn : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                lifecycleScope.launch {
                    val flag = UsersData().loginUser(EmailPass(email, pass))

                    if (flag.second) {
                        val editor = getSharedPreferences("myPrefs", MODE_PRIVATE).edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.putInt("id", flag.first)
                        editor.apply()
                        val intent = Intent(this@SignIn, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@SignIn,
                            "Не существует такого пользователя или был введен неправильный пароль",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    override fun onStart() {
        super.onStart()
    }
}