package ru.guzeevmd.activediabetesassistant.data.client

import android.media.tv.CommandResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
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
    private val authToken = "edeae676b79041e2f0bc6e8a8aa4d9c224b634d5c99a46403a72914684575a3a1d8ab323735410b6d7e9e6f57097e5e6b9dba04848ba4b1544187b1f5475b4e388a248732e95169810661f6ba53c0b0d49eae3d05c08dd6c6d04c97b56d877cec3039d4ac987fabb7e2ada831c98e4221aeb41680ad6d382ab05b2e9344b64ab2bbf82941d5ceeb63ce2c055f6ee981614e2d69d052878f46a6bd18ae825209ee35890d97d4c984c8e1e20301357be088bdf4ec3e3a98b9951bc3ba4280c025a444a8e3a9c91ca48e29d150276dc91396ba67420c20f671e022e9bc02ed81638b3d6acb7db3840089aaa5518eea5edc0ee822a344bef963bc96542cbe463712c67561b0443eef8d94a3cefabf92574a9c8c3015bd6eac5fcfdc1e2b1dfd2e1ecac28de65182b3e9ba8e1406ed5e5210c1a9798189038666dd7b443d7990a1389dd76094e82d2a2515b334b8db89d10639c2c1b6bd05362478b9b21eddfa033ed144b5f348c40e848c2ca10e3541c573e42439fc348697fef0d81c0b2c934a43e"

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
                allowSpecialFloatingPointValues = true
                useArrayPolymorphism = true
            })
        }

        defaultRequest {
            host = baseUrl
            url {
                protocol = URLProtocol.HTTPS
            }
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
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

    suspend fun getPersonInfo(): PersonInfoViewModel? =
        client.get("$baseUrl/PersonInfo/GetMy") {
        }.let { response ->
            if (response.status.isSuccess()) {
                Json.decodeFromString<PersonInfoViewModel>(response.bodyAsText())
            } else {
                null // Handle errors or throw an exception as needed
            }
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
