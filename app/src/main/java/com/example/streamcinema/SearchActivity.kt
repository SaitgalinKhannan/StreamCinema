package com.example.streamcinema

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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

class SearchActivity : AppCompatActivity() {

    private val moviesData = MoviesData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchButton: Button = findViewById(R.id.button)
        val searchText: EditText = findViewById(R.id.search_edit_text)

        val movies = mutableListOf<Movie>()

        val movieAdapter = SearchAdapter(movies, moviesData, this)
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

        searchButton.setOnClickListener {
            if (searchText.text.isNotEmpty()) {
                Toast.makeText(applicationContext, "${searchText.text}", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    val newList = moviesData.searchMovieByTitle(searchText.text.toString())
                    movieAdapter.updateData(newList)
                }
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.searchButton

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mainButton -> {
                    startActivity(Intent(this@SearchActivity, MainActivity::class.java))
                    true
                }

                R.id.bookmarkButton -> {
                    startActivity(Intent(this@SearchActivity, UserMoviesActivity::class.java))
                    true
                }

                R.id.searchButton -> {
                    startActivity(Intent(this@SearchActivity, SearchActivity::class.java))
                    true
                }

                R.id.profileButton -> {
                    startActivity(Intent(this@SearchActivity, ProfileActivity::class.java))
                    true
                }

                else -> false
            }
        }

    }
}

class SearchAdapter(
    private val moviesList: MutableList<Movie>,
    private val moviesData: MoviesData,
    private val context: Context
) :
    RecyclerView.Adapter<SearchAdapter.MovieViewHolder>() {

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