package com.example.streamcinema

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.streamcinema.databinding.RvCardViewBinding
import com.example.streamcinema.model.Movie
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val moviesData = MoviesData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val movies = mutableListOf<Movie>()

        val movieAdapter = MovieAdapter(movies, moviesData)

        val layoutManager = GridLayoutManager(this, 2)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = movieAdapter

        lifecycleScope.launch {
            val newList = moviesData.fullMovies().subList(0, 2)
            movieAdapter.updateData(newList) // обновите адаптер с новыми данными
        }
    }
}

class MovieAdapter(
    private val moviesList: MutableList<Movie>,
    private val moviesData: MoviesData
) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(private val binding: RvCardViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.titleTextView.text = movie.title
            //binding.imageView.setImageResource()
            try {
                /*Picasso.get()
                    .load("https://media.geeksforgeeks.org/wp-content/uploads/20210101144014/gfglogo.png")
                    .into(binding.imageView)*/
                Picasso.get()
                    .load("http://192.168.24.116:8080/movie/preview/${movie.id}")
                    .into(binding.imageView)
            } catch (e: Exception) {
                Log.d("Picasso", e.message.toString())
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

    fun updateData (newData: List<Movie>) {
        moviesList.clear () // очистите старые данные
        moviesList.addAll (newData) // добавьте новые данные
        notifyDataSetChanged() // уведомите адаптер об изменениях
    }
}