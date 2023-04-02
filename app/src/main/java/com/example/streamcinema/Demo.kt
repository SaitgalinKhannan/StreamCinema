package com.example.streamcinema

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso


class Demo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        val imageView = findViewById<ImageView>(R.id.imageView)
        Picasso.get()
            .load("http://192.168.24.116:8080/movie/preview/1")
            .into(imageView)
    }
}