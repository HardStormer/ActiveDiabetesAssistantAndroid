package ru.guzeevmd.activediabetesassistant.data.client

import android.media.tv.CommandResponse
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.guzeevmd.activediabetesassistant.data.models.CreateGlucoseInfoCommand
import ru.guzeevmd.activediabetesassistant.data.models.CreateMyPersonInfoCommand
import ru.guzeevmd.activediabetesassistant.data.models.GlucoseInfoViewModel
import ru.guzeevmd.activediabetesassistant.data.models.LoginUserCommand
import ru.guzeevmd.activediabetesassistant.data.models.PersonInfoViewModel
import ru.guzeevmd.activediabetesassistant.data.models.RegisterUserCommand
import ru.guzeevmd.activediabetesassistant.data.models.UpdateMyPersonInfoCommand
import ru.guzeevmd.activediabetesassistant.data.models.UserViewModel

class DiabetesAssistantApiClient() {
    private val baseUrl = "https://hardstormer-activediabetesassistant-b648.twc1.net"
    private val authToken = ""
    private val client = HttpClient() {
        install(ContentNegotiation) {
            json(Json {
                // Configure JSON handling:
                isLenient = true
                ignoreUnknownKeys = true
                allowSpecialFloatingPointValues = true
                useArrayPolymorphism = true
            })
        }
        install(DefaultRequest){
            header(HttpHeaders.Authorization, "Bearer $authToken")
        }
    }

    //User
    suspend fun getMyUser(): UserViewModel? =
        client.get("$baseUrl/User/GetMy") {
        }.let { response ->
            if (response.status.isSuccess()) {
                Json.decodeFromString<UserViewModel>(response.bodyAsText())
            } else {
                null // Handle errors or throw an exception as needed
            }
        }

    suspend fun registerUser(credentials: RegisterUserCommand): CommandResponse? =
        client.post("$baseUrl/User/Register") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }.let { response ->
            if (response.status.isSuccess()) {
                Json.decodeFromString<CommandResponse>(response.bodyAsText())
            } else {
                null // Handle errors or throw an exception as needed
            }
        }

    suspend fun loginUser(credentials: LoginUserCommand): CommandResponse? =
        client.post("$baseUrl/User/LogIn") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }.let { response ->
            if (response.status.isSuccess()) {
                Json.decodeFromString<CommandResponse>(response.bodyAsText())
            } else {
                null // Handle errors or throw an exception as needed
            }
        }

    suspend fun logoutUser(): HttpResponse =
        client.post("$baseUrl/User/LogOut")

    //Person
    suspend fun createPersonInfo(info: CreateMyPersonInfoCommand): PersonInfoViewModel? =
        client.post("$baseUrl/PersonInfo/Create") {
            contentType(ContentType.Application.Json)
            setBody(info)
        }.let { response ->
            if (response.status.isSuccess()) {
                Json.decodeFromString<PersonInfoViewModel>(response.bodyAsText())
            } else {
                null // Handle errors or throw an exception as needed
            }
        }

    suspend fun updatePersonInfo(command: UpdateMyPersonInfoCommand): HttpResponse =
        client.post("$baseUrl/PersonInfo/Update") {
            contentType(ContentType.Application.Json)
            setBody(command)
        }

    //GlucoseInfo
    suspend fun getGlucoseInfo(id: String): GlucoseInfoViewModel? =
        client.get("$baseUrl/GlucoseInfo/Get") {
            parameter("Id", id)
        }.let { response ->
            if (response.status.isSuccess()) {
                Json.decodeFromString<GlucoseInfoViewModel>(response.bodyAsText())
            } else {
                null // Handle errors or throw an exception as needed
            }
        }

    suspend fun getGlucoseInfoCollection(limit: String, offset: String): Collection<GlucoseInfoViewModel>? =
        client.get("$baseUrl/GlucoseInfo/Get") {
            parameter("limit", limit)
            parameter("offset", offset)
        }.let { response ->
            if (response.status.isSuccess()) {
                Json.decodeFromString<Collection<GlucoseInfoViewModel>>(response.bodyAsText())
            } else {
                null // Handle errors or throw an exception as needed
            }
        }

    suspend fun createGlucoseInfo(glucoseInfo: CreateGlucoseInfoCommand): GlucoseInfoViewModel? =
        client.post("$baseUrl/GlucoseInfo/Create") {
            contentType(ContentType.Application.Json)
            setBody(glucoseInfo)
        }.let { response ->
            if (response.status.isSuccess()) {
                Json.decodeFromString<GlucoseInfoViewModel>(response.bodyAsText())
            } else {
                null // Handle errors or throw an exception as needed
            }
        }

}
