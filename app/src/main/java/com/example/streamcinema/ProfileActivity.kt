package com.example.streamcinema

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.streamcinema.databinding.ActivityProfileBinding
import com.example.streamcinema.databinding.ActivitySignUpBinding
import com.example.streamcinema.model.CinemaUser
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val userId = prefs.getInt("id", 0)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            try {
                val userData = UsersData().userData(userId)
                updateUserData(userData)

            } catch (e: Exception) {
                Log.d("Exception", e.message.toString())
                Toast.makeText(this@ProfileActivity, userId.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        binding.exitButton.setOnClickListener {
            val editor = getSharedPreferences("myPrefs", MODE_PRIVATE).edit()
            editor.putBoolean("isLoggedIn", false)
            editor.putInt("id", 0)
            editor.apply()
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val firstName = binding.firstNameEt.text.toString()
            val middleName = binding.middleNameEt.text.toString()
            val lastName = binding.lastNameEt.text.toString()
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (firstName.isNotEmpty() && middleName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        UsersData().updateUser(
                            CinemaUser(
                                userId,
                                lastName,
                                firstName,
                                middleName,
                                email,
                                pass
                            )
                        )

                        Toast.makeText(
                            this@ProfileActivity,
                            "Данные успешно обновлены!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@ProfileActivity, "Ошибка!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.profileButton

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mainButton -> {
                    startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
                    true
                }

                R.id.bookmarkButton -> {
                    startActivity(Intent(this@ProfileActivity, UserMoviesActivity::class.java))
                    true
                }

                R.id.searchButton -> {
                    startActivity(Intent(this@ProfileActivity, SearchActivity::class.java))
                    true
                }

                R.id.profileButton -> {
                    startActivity(Intent(this@ProfileActivity, ProfileActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun updateUserData(cinemaUser: CinemaUser) {
        with(cinemaUser) {
            binding.firstNameEt.setText(firstName)
            binding.middleNameEt.setText(middleName)
            binding.lastNameEt.setText(lastName)
            binding.emailEt.setText(email)
            binding.passET.setText(password)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}