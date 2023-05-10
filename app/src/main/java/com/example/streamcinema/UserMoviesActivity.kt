package com.example.streamcinema

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.streamcinema.databinding.RvCardViewBinding
import com.example.streamcinema.model.Movie
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class UserMoviesActivity : AppCompatActivity() {

    private val moviesData = MoviesData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val movies = mutableListOf<Movie>()

        val prefs = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val userId = prefs.getInt("id", 0)

        val movieAdapter = UserMovieAdapter(movies, moviesData, this)
        movieAdapter.setOnItemClickListener(@UnstableApi object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(applicationContext, MovieActivity::class.java).apply {
                    val previewId = movies[position].id
                    putExtra("id", previewId)
                }
                startActivity(intent)
            }
        })

        val layoutManager = GridLayoutManager(this, 2)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = movieAdapter

        lifecycleScope.launch {
            val newList = moviesData.movieByUser(userId)
            movieAdapter.updateData(newList)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.bookmarkButton

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mainButton -> {
                    startActivity(Intent(this@UserMoviesActivity, MainActivity::class.java))
                    true
                }

                R.id.bookmarkButton -> {
                    startActivity(Intent(this@UserMoviesActivity, UserMoviesActivity::class.java))
                    true
                }

                R.id.searchButton -> {
                    startActivity(Intent(this@UserMoviesActivity, SearchActivity::class.java))
                    true
                }

                R.id.profileButton -> {
                    startActivity(Intent(this@UserMoviesActivity, Demo::class.java))
                    true
                }

                else -> false
            }
        }

    }
}

class UserMovieAdapter(
    private val moviesList: MutableList<Movie>,
    private val moviesData: MoviesData,
    private val context: Context
) :
    RecyclerView.Adapter<UserMovieAdapter.MovieViewHolder>() {

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
