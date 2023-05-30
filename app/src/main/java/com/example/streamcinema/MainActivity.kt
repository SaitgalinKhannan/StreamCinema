package com.example.streamcinema

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.streamcinema.databinding.RvCardViewBinding
import com.example.streamcinema.model.Movie
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val moviesData = MoviesData()
    private val backPressDelay = 2000
    private var backPressTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)

        if (!isLoggedIn) {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }

        val movies = mutableListOf<Movie>()

        val movieAdapter = MovieAdapter(movies, moviesData, this)
        movieAdapter.setOnItemClickListener(@UnstableApi object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(applicationContext, MovieActivity::class.java).apply {
                    val previewId = movies[position].id
                    putExtra("movieId", previewId)
                }
                startActivity(intent)
            }
        })

        val layoutManager = GridLayoutManager(this, 2)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = movieAdapter

        lifecycleScope.launch {
            val newList = moviesData.fullMovies()
            movieAdapter.updateData(newList)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.mainButton

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mainButton -> {
                    startActivity(Intent(this@MainActivity, MainActivity::class.java))
                    true
                }

                R.id.bookmarkButton -> {
                    startActivity(Intent(this@MainActivity, UserMoviesActivity::class.java))
                    true
                }

                R.id.searchButton -> {
                    startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                    true
                }

                R.id.profileButton -> {
                    startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (backPressTime + backPressDelay > currentTime) {
            finishAffinity()
        } else {
            backPressTime = currentTime
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
        }
    }
}

interface OnItemClickListener {
    fun onItemClick(position: Int)
}

class MovieAdapter(
    private val moviesList: MutableList<Movie>,
    private val moviesData: MoviesData,
    private val context: Context
) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var clickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    inner class MovieViewHolder(private val binding: RvCardViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                clickListener?.onItemClick(adapterPosition)
            }
        }

        fun bind(movie: Movie) {
            binding.titleTextView.text = movie.title
            try {
                Glide.with(context).load(moviesData.moviePreview(movie.id)).into(binding.imageView)
            } catch (e: Exception) {
                Log.d("Glide", e.message.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = RvCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val fullMovie = moviesList[position]
        holder.bind(fullMovie)
    }

    override fun getItemCount(): Int = moviesList.size

    fun updateData(newData: List<Movie>) {
        moviesList.clear()
        moviesList.addAll(newData)
        notifyDataSetChanged()
    }
}