package com.example.streamcinema

import com.example.streamcinema.model.MoviesList
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MoviesData {
    private val client = HttpClient {
        Json{
            ignoreUnknownKeys = true
        }
    }
    private val url = "192.168.0.104"

    suspend fun movieData(): MoviesList = withContext(Dispatchers.IO) {
        val response = client.get("$url/movie/all")
        val body = response.bodyAsText()
        return@withContext  Json.decodeFromString<MoviesList>(body)
    }

    suspend fun moviesPreview() = withContext(Dispatchers.IO) {

    }
}