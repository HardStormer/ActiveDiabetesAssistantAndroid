package ru.guzeevmd.activediabetesassistant.data.client

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
import ru.guzeevmd.activediabetesassistant.data.models.LoginUserCommandResponse
import ru.guzeevmd.activediabetesassistant.data.models.PersonInfoViewModel
import ru.guzeevmd.activediabetesassistant.data.models.RegisterUserCommand
import ru.guzeevmd.activediabetesassistant.data.models.UpdateMyPersonInfoCommand
import ru.guzeevmd.activediabetesassistant.data.models.UserViewModel

class DiabetesAssistantApiClient(private val authToken: String?) {
    private val baseUrl = "https://hardstormer-activediabetesassistant-b648.twc1.net"

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
            if(authToken != null){
                header(HttpHeaders.Authorization, "Bearer $authToken")
            }
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

    suspend fun registerUser(credentials: RegisterUserCommand): LoginUserCommandResponse? =
        client.post("$baseUrl/User/Register") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }.let { response ->
            if (response.status.isSuccess()) {
                Json.decodeFromString<LoginUserCommandResponse>(response.bodyAsText())
            } else {
                null // Handle errors or throw an exception as needed
            }
        }

    suspend fun loginUser(credentials: LoginUserCommand): LoginUserCommandResponse? =
        client.post("$baseUrl/User/LogIn") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }.let { response ->
            if (response.status.isSuccess()) {
                Json.decodeFromString<LoginUserCommandResponse>(response.bodyAsText())
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
