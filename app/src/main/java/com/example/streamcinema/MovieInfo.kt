package com.example.streamcinema

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.streamcinema.model.MovieFullInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@UnstableApi
class MovieActivity : AppCompatActivity() {
    private lateinit var movieWatch: Button
    private lateinit var moviePoster: ImageView
    private lateinit var movieTitle: TextView
    private lateinit var movieYear: TextView
    private lateinit var movieDuration: TextView
    private lateinit var movieCountry: TextView
    private lateinit var movieLanguage: TextView
    private lateinit var movieCast: TextView
    private lateinit var movieDirectors: TextView
    private val moviesData = MoviesData()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_info)
        movieWatch = findViewById(R.id.movie_watch)
        moviePoster = findViewById(R.id.movie_poster)
        movieTitle = findViewById(R.id.movie_title)
        movieYear = findViewById(R.id.movie_year)
        movieDuration = findViewById(R.id.movie_duration)
        movieCountry = findViewById(R.id.movie_country)
        movieLanguage = findViewById(R.id.movie_language)
        movieCast = findViewById(R.id.movie_cast)
        movieDirectors = findViewById(R.id.movie_directors)

        val movieId = intent.getIntExtra("MOVIE_ID", 0)

        scope.launch {
            val movieFullInfo = moviesData.movieFullInfo(1)
            Glide.with(applicationContext).load(moviesData.moviePreview(movieId)).into(moviePoster)
            movieTitle.text = movieFullInfo.movie.title
            movieYear.text = movieFullInfo.movie.releaseYear.toString()
            movieDuration.text = movieFullInfo.movie.runtimeMinutes.toString()
            movieCountry.text = movieFullInfo.movie.releaseCountry
            movieLanguage.text =  movieFullInfo.movie.language
        }

        movieWatch.setOnClickListener {
            val intent = Intent(applicationContext, WatchVideoActivity::class.java).apply {
                putExtra("VIDEO_URL", moviesData.movieFile(movieId))
            }
            startActivity(intent)
        }
    }
}
