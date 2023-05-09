package com.example.streamcinema

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.launch
import okhttp3.internal.wait


@UnstableApi
class MovieActivity : AppCompatActivity() {
    private lateinit var movieWatch: Button
    private lateinit var moviePoster: ImageView
    private lateinit var movieTitle: TextView
    private lateinit var movieInfo: TextView
    private lateinit var movieCast: Button
    private lateinit var movieSave: Button
    private val moviesData = MoviesData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_info)
        movieWatch = findViewById(R.id.movie_watch)
        moviePoster = findViewById(R.id.movie_poster)
        movieTitle = findViewById(R.id.movie_title)
        movieInfo = findViewById(R.id.movie_info)
        movieCast = findViewById(R.id.movie_cast)
        movieSave = findViewById(R.id.movie_save)

        val prefs = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val userId = prefs.getInt("id", 0)
        val movieId = intent.getIntExtra("movieId", 1)
        //Toast.makeText(applicationContext, "$userId $movieId", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            val movieFullInfo = moviesData.movieFullInfo(movieId)
            Glide.with(applicationContext)
                .load(moviesData.moviePreview(movieId))
                .into(moviePoster)
            movieTitle.text = movieFullInfo.movie.title
            movieInfo.text =
                "${movieFullInfo.movie.releaseCountry.uppercase()}, ${movieFullInfo.movie.releaseYear}, ${movieFullInfo.movie.runtimeMinutes} мин., ${movieFullInfo.movie.language.uppercase()}"
        }

        movieWatch.setOnClickListener {
            val intent = Intent(applicationContext, WatchVideoActivity::class.java).apply {
                putExtra("VIDEO_URL", moviesData.movieFile(movieId))
            }
            startActivity(intent)
        }

        movieCast.setOnClickListener {
            val intent = Intent(this, CastActivity::class.java).apply {
                putExtra("id", movieId)
            }
            startActivity(intent)
        }

        movieSave.setOnClickListener {
            lifecycleScope.launch {
                moviesData.insertMovie(Pair(userId, movieId))
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_UNLABELED

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mainButton -> {
                    startActivity(Intent(this@MovieActivity, MainActivity::class.java))
                    true
                }

                R.id.bookmarkButton -> {
                    startActivity(Intent(this@MovieActivity, UserMoviesActivity::class.java))
                    true
                }

                R.id.searchButton -> {
                    startActivity(Intent(this@MovieActivity, SearchActivity::class.java))
                    true
                }

                R.id.profileButton -> {
                    startActivity(Intent(this@MovieActivity, Demo::class.java))
                    true
                }

                else -> false
            }
        }

    }
}

