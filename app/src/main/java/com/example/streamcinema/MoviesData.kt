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
    private val url = "http://192.168.24.116:8080"

    suspend fun fullMovies(): List<Movie> = withContext(Dispatchers.IO) {
        val moviesResponse = client.get("http://192.168.24.116:8080/movie/all")
        Log.d("RESPONSE", moviesResponse.toString())
        return@withContext Json.decodeFromString<List<Movie>>(moviesResponse.body())
    }

    fun moviePreview(id: Int) = "${url}movie/preview/$id"
}