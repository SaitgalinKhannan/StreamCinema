package com.example.streamcinema

import android.util.Log
import com.example.streamcinema.model.CinemaUser
import com.example.streamcinema.model.EmailPass
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UsersData {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }
    private val url = "http://192.168.34.116:9090"

    suspend fun registerUser(emailPass: EmailPass): String = withContext(Dispatchers.IO) {
        /*val text = Json.encodeToString(
            CinemaUser(
                1,
                "Test",
                "Test",
                " Test",
                emailPass.email,
                emailPass.password
            )
        )
        Log.d("JSON", text)*/

        val data = CinemaUser(
            1,
            "Test",
            "Test",
            " Test",
            emailPass.email,
            emailPass.password
        )

        val response = client.post("http://$url/register") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }

        return@withContext response.bodyAsText()
    }

    suspend fun loginUser(emailPass: EmailPass): Pair<Int, Boolean> = withContext(Dispatchers.IO) {
        val response = client.post("http://$url/login") {
            contentType(ContentType.Application.Json)
            setBody(emailPass)
        }

        Log.d("JSON", response.bodyAsText())
        return@withContext Json.decodeFromString(response.bodyAsText())
    }
}