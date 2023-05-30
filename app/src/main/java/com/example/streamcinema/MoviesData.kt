package com.example.streamcinema

import com.example.streamcinema.model.Actor
import com.example.streamcinema.model.Movie
import com.example.streamcinema.model.MovieFullInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MoviesData {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json)
        }
    }

    private var url = "http://192.168.253.116:9090"

    suspend fun searchMovieByTitle(title: String): List<Movie> = withContext(Dispatchers.IO) {
        val moviesResponse = client.post("${url}/movie/title") {
            contentType(ContentType.Application.Json)
            setBody(title)
        }
        return@withContext try {
            Json.decodeFromString<List<Movie>>(moviesResponse.body<String>())
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fullMovies(): List<Movie> = withContext(Dispatchers.IO) {
        val moviesResponse = client.get("${url}/movie/all")
        return@withContext try {
            Json.decodeFromString<List<Movie>>(moviesResponse.body())
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun movieByUser(id: Int): List<Movie> = withContext(Dispatchers.IO) {
        val moviesResponse = client.get("${url}/movie/usermovie/$id")
        return@withContext try {
            Json.decodeFromString<List<Movie>>(moviesResponse.body())
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun insertMovie(pair: Pair<Int, Int>) = withContext(Dispatchers.IO) {
        client.post("$url/movie/usermovie/insert") {
            contentType(ContentType.Application.Json)
            setBody(pair)
        }
    }

    suspend fun deleteMovie(pair: Pair<Int, Int>) = withContext(Dispatchers.IO) {
        client.post("$url/movie/usermovie/delete") {
            contentType(ContentType.Application.Json)
            setBody(pair)
        }
    }

    suspend fun movieFullInfo(id: Int): MovieFullInfo = withContext(Dispatchers.IO) {
        val movieFullInfoResponse = client.get("${url}/movie/full/$id")
        return@withContext try {
            Json.decodeFromString<MovieFullInfo>(movieFullInfoResponse.body())
        } catch (e: Exception) {
            throw Exception("Ошибка!")
        }
    }

    suspend fun movieCast(id: Int): List<Actor> = withContext(Dispatchers.IO) {
        val movieCast = client.get("${url}/movie/cast/$id")
        return@withContext try {
            Json.decodeFromString<List<Actor>>(movieCast.body())
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun moviePreview(id: Int) = "$url/movie/preview/$id"

    fun movieFile(id: Int) = "$url/movie/watch/$id"
}