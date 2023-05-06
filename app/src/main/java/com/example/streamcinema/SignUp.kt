package com.example.streamcinema

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.streamcinema.databinding.ActivitySignUpBinding
import com.example.streamcinema.model.EmailPass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    lifecycleScope.launch {
                        val flag = UsersData().registerUser(EmailPass(email, pass))
                        if (flag == "true") {
                            Toast.makeText(this@SignUp, "Вы успешно зарегистрировались!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@SignUp, SignIn::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@SignUp, "Такой пользователь уже существует", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}