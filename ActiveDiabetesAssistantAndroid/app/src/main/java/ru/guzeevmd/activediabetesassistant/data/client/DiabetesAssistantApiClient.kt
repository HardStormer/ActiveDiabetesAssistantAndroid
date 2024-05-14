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
import ru.guzeevmd.activediabetesassistant.data.models.DeleteGlucoseInfoCommand
import ru.guzeevmd.activediabetesassistant.data.models.GlucoseInfoViewModel
import ru.guzeevmd.activediabetesassistant.data.models.ListResponse
import ru.guzeevmd.activediabetesassistant.data.models.LoginUserCommand
import ru.guzeevmd.activediabetesassistant.data.models.PersonInfoViewModel
import ru.guzeevmd.activediabetesassistant.data.models.RegisterUserCommand
import ru.guzeevmd.activediabetesassistant.data.models.UpdateMyPersonInfoCommand
import ru.guzeevmd.activediabetesassistant.data.models.UserViewModel

class DiabetesAssistantApiClient() {
    private val baseUrl = "https://hardstormer-activediabetesassistant-b648.twc1.net"
    private val authToken =
        "a52b2bc17bc4aab3bb9524e0e5d84f546a9b941b074732abc1f20569c12928433db22de4dce429d76f6bc41012f455c4472e41ca1e7a04f7b0a2dc9df45d779464ece2030e1bceb279c6de2c29451bc0f8b83bb33462e82fe9491a556e2a698acc9f99561fed336345229a0cd59eeb40fccb7e16ae8b4198ad19e686df124d64cfee2938e6870190bd0ba02773b90f3ef39dbcd9d488523584cab2266d732d4aaaa5d7f3e6dc080b68b32394883c8d3bac559c48388c80218e6d97f8f0421df6bc0dea44742d52a9048e5b7cc4e7fcba619298a43eec0aacaaf95c3bb6604aa348df33d1324fc1964f6e07398ef31ae183c00cb24e6be8ae08f1e900e2b50e57ca8824138848a1e2752eceb3290e72ef40d7324cb15828963c407b1acee30445b53718e9e3e923fc6c8fc6d3131fc19f5968691ef3caebd80b1b7420b9499037b4c7171c7d82490aab9ac012b58379a2f52d90f6a460169acbec72ab4faec67d31b1d220b54e5aa139b780bd81847ecf9c0d1a3447bf0b6d430e789cd34ddc5d"

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

    suspend fun getGlucoseInfoCollection(
        limit: Int,
        offset: Int
    ): ListResponse<GlucoseInfoViewModel> =
        client.get("$baseUrl/GlucoseInfo/GetList") {
            parameter("limit", limit)
            parameter("offset", offset)
        }.let { response ->
            if (response.status.isSuccess()) {
                Json.decodeFromString<ListResponse<GlucoseInfoViewModel>>(response.bodyAsText())
            } else {
                ListResponse(emptyList(), 0)
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

    suspend fun deleteGlucoseInfo(command: DeleteGlucoseInfoCommand): HttpResponse =
        client.post("$baseUrl/GlucoseInfo/Delete") {
            contentType(ContentType.Application.Json)
            setBody(command)
        }

}
