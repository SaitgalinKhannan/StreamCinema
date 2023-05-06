package com.example.streamcinema

import android.util.Log
import com.example.streamcinema.model.Movie
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MoviesData {
    private val client = HttpClient(CIO) {
        Json {
            ignoreUnknownKeys = true
        }
    }
    private val url = R.string.url

    suspend fun fullMovies(): List<Movie> = withContext(Dispatchers.IO) {
        val moviesResponse = client.get("${url}/movie/all")
        Log.d("RESPONSE", moviesResponse.toString())
        return@withContext Json.decodeFromString<List<Movie>>(moviesResponse.body())
    }

    fun moviePreview(id: Int) = "$url/movie/preview/$id"

    fun movieFile(id: Int) = "$url/movie/watch/$id"
}