package ru.guzeevmd.activediabetesassistant.data.client

import android.graphics.Bitmap
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import kotlinx.serialization.json.Json
import ru.guzeevmd.activediabetesassistant.data.models.AskAiCommand
import ru.guzeevmd.activediabetesassistant.data.models.CheckTokenQuery
import ru.guzeevmd.activediabetesassistant.data.models.CreateGlucoseInfoCommand
import ru.guzeevmd.activediabetesassistant.data.models.CreateMyPersonInfoCommand
import ru.guzeevmd.activediabetesassistant.data.models.DeleteGlucoseInfoCommand
import ru.guzeevmd.activediabetesassistant.data.models.GlucoseInfoViewModel
import ru.guzeevmd.activediabetesassistant.data.models.ListResponse
import ru.guzeevmd.activediabetesassistant.data.models.LoginUserCommand
import ru.guzeevmd.activediabetesassistant.data.models.LoginUserCommandResponse
import ru.guzeevmd.activediabetesassistant.data.models.OcrResponse
import ru.guzeevmd.activediabetesassistant.data.models.PersonInfoViewModel
import ru.guzeevmd.activediabetesassistant.data.models.ProblemDetails
import ru.guzeevmd.activediabetesassistant.data.models.RegisterUserCommand
import ru.guzeevmd.activediabetesassistant.data.models.UpdateMyPersonInfoCommand
import ru.guzeevmd.activediabetesassistant.data.models.UserViewModel
import ru.guzeevmd.activediabetesassistant.data.models.ValidationProblemDetails
import java.io.ByteArrayOutputStream


sealed class ApiError {
    data class Problem(val details: ProblemDetails) : ApiError()
    data class ValidationProblem(val details: ValidationProblemDetails) : ApiError()
}


class DiabetesAssistantApiClient(private val authToken: String?) {
    private val baseUrl = "https://hardstormer-activediabetesassistant-8de0.twc1.net"

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
            if (authToken != null) {
                header(HttpHeaders.Authorization, "Bearer $authToken")
            }
        }
    }

    private suspend fun parseErrorResponse(response: HttpResponse): ApiError? {
        return try {
            val validationProblemDetails = Json.decodeFromString<ValidationProblemDetails>(response.bodyAsText())
            ApiError.ValidationProblem(validationProblemDetails)
        } catch (e: Exception) {
            try {
                val problemDetails = Json.decodeFromString<ProblemDetails>(response.bodyAsText())
                ApiError.Problem(problemDetails)
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun handleApiError(status: HttpStatusCode, error: ApiError?) {
        val tag = "DiabetesApiClient"
        when (status) {
            HttpStatusCode.BadRequest -> {
                when (error) {
                    is ApiError.ValidationProblem -> Log.e(tag, "Validation Errors: ${error.details.errors}")
                    is ApiError.Problem -> Log.e(tag, "Bad Request: ${error.details.detail}")
                    else -> Log.e(tag, "Bad Request: No details available")
                }
            }
            HttpStatusCode.Unauthorized -> Log.e(tag, "Unauthorized: ${(error as? ApiError.Problem)?.details?.detail}")
            HttpStatusCode.Forbidden -> Log.e(tag, "Forbidden: ${(error as? ApiError.Problem)?.details?.detail}")
            HttpStatusCode.NotFound -> Log.e(tag, "Not Found: ${(error as? ApiError.Problem)?.details?.detail}")
            HttpStatusCode.InternalServerError -> Log.e(tag, "Internal Server Error: ${(error as? ApiError.Problem)?.details?.detail}")
            else -> Log.e(tag, "Error ${status.value}: ${(error as? ApiError.Problem)?.details?.detail}")
        }
    }

    suspend fun checkToken(query: CheckTokenQuery): Boolean? {
        val response = client.get("$baseUrl/User/CheckToken")
        return when {
            response.status.isSuccess() -> {
                val result = response.bodyAsText().toBoolean()
                Log.i("DiabetesApiClient", "Token check result: $result")
                result
            }
            else -> {
                val error = parseErrorResponse(response)
                handleApiError(response.status, error)
                null
            }
        }
    }
    suspend fun askAi(command: AskAiCommand): String {
        val response = client.post("$baseUrl/Ai/Ask") {
            contentType(ContentType.Application.Json)
            setBody(command)
        }
        return when {
            response.status.isSuccess() -> {
                response.bodyAsText()
            }
            else -> {
                val error = parseErrorResponse(response)
                handleApiError(response.status, error)
                "AI ERROR"
            }
        }
    }
    suspend fun getMyUser(): UserViewModel? {
        val response = client.get("$baseUrl/User/GetMy")
        return when {
            response.status.isSuccess() -> {
                val user = Json.decodeFromString<UserViewModel>(response.bodyAsText())
                Log.i("DiabetesApiClient", "User retrieved successfully: $user")
                user
            }
            else -> {
                val error = parseErrorResponse(response)
                handleApiError(response.status, error)
                null
            }
        }
    }

    suspend fun registerUser(credentials: RegisterUserCommand): LoginUserCommandResponse? {
        val response = client.post("$baseUrl/User/Register") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }
        return when {
            response.status.isSuccess() -> {
                val loginResponse = Json.decodeFromString<LoginUserCommandResponse>(response.bodyAsText())
                Log.i("DiabetesApiClient", "User registered successfully: $loginResponse")
                loginResponse
            }
            else -> {
                val error = parseErrorResponse(response)
                handleApiError(response.status, error)
                null
            }
        }
    }

    suspend fun loginUser(credentials: LoginUserCommand): LoginUserCommandResponse? {
        val response = client.post("$baseUrl/User/LogIn") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }
        return when {
            response.status.isSuccess() -> {
                val loginResponse = Json.decodeFromString<LoginUserCommandResponse>(response.bodyAsText())
                Log.i("DiabetesApiClient", "User logged in successfully: $loginResponse")
                loginResponse
            }
            else -> {
                val error = parseErrorResponse(response)
                handleApiError(response.status, error)
                null
            }
        }
    }

    suspend fun logoutUser(): HttpResponse {
        return client.post("$baseUrl/User/LogOut").also { response ->
            if (response.status.isSuccess()) {
                Log.i("DiabetesApiClient", "User logged out successfully")
            } else {
                val error = parseErrorResponse(response)
                handleApiError(response.status, error)
            }
        }
    }

    suspend fun createPersonInfo(info: CreateMyPersonInfoCommand): PersonInfoViewModel? {
        val response = client.post("$baseUrl/PersonInfo/Create") {
            contentType(ContentType.Application.Json)
            setBody(info)
        }
        return when {
            response.status.isSuccess() -> {
                val personInfo = Json.decodeFromString<PersonInfoViewModel>(response.bodyAsText())
                Log.i("DiabetesApiClient", "Person info created successfully: $personInfo")
                personInfo
            }
            else -> {
                val error = parseErrorResponse(response)
                handleApiError(response.status, error)
                null
            }
        }
    }

    suspend fun updatePersonInfo(command: UpdateMyPersonInfoCommand): HttpResponse {
        return client.post("$baseUrl/PersonInfo/Update") {
            contentType(ContentType.Application.Json)
            setBody(command)
        }.also { response ->
            if (response.status.isSuccess()) {
                Log.i("DiabetesApiClient", "Person info updated successfully")
            } else {
                val error = parseErrorResponse(response)
                handleApiError(response.status, error)
            }
        }
    }

    suspend fun getPersonInfo(): PersonInfoViewModel? {
        val response = client.get("$baseUrl/PersonInfo/GetMy")
        return when {
            response.status.isSuccess() -> {
                val personInfo = Json.decodeFromString<PersonInfoViewModel>(response.bodyAsText())
                Log.i("DiabetesApiClient", "Person info retrieved successfully: $personInfo")
                personInfo
            }
            else -> {
                val error = parseErrorResponse(response)
                handleApiError(response.status, error)
                null
            }
        }
    }

    suspend fun getGlucoseInfo(id: String): GlucoseInfoViewModel? {
        val response = client.get("$baseUrl/GlucoseInfo/Get") {
            parameter("Id", id)
        }
        return when {
            response.status.isSuccess() -> {
                val glucoseInfo = Json.decodeFromString<GlucoseInfoViewModel>(response.bodyAsText())
                Log.i("DiabetesApiClient", "Glucose info retrieved successfully: $glucoseInfo")
                glucoseInfo
            }
            else -> {
                val error = parseErrorResponse(response)
                handleApiError(response.status, error)
                null
            }
        }
    }

    suspend fun getGlucoseInfoCollection(limit: Int, offset: Int): ListResponse<GlucoseInfoViewModel> {
        val response = client.get("$baseUrl/GlucoseInfo/GetList") {
            parameter("limit", limit)
            parameter("offset", offset)
        }
        return when {
            response.status.isSuccess() -> {
                val glucoseInfoList = Json.decodeFromString<ListResponse<GlucoseInfoViewModel>>(response.bodyAsText())
                Log.i("DiabetesApiClient", "Glucose info collection retrieved successfully")
                glucoseInfoList
            }
            else -> {
                val error = parseErrorResponse(response)
                handleApiError(response.status, error)
                ListResponse(emptyList(), 0)
            }
        }
    }

    suspend fun createGlucoseInfo(glucoseInfo: CreateGlucoseInfoCommand): GlucoseInfoViewModel? {
        val response = client.post("$baseUrl/GlucoseInfo/Create") {
            contentType(ContentType.Application.Json)
            setBody(glucoseInfo)
        }
        return when {
            response.status.isSuccess() -> {
                val createdGlucoseInfo = Json.decodeFromString<GlucoseInfoViewModel>(response.bodyAsText())
                Log.i("DiabetesApiClient", "Glucose info created successfully: $createdGlucoseInfo")
                createdGlucoseInfo
            }
            else -> {
                val error = parseErrorResponse(response)
                handleApiError(response.status, error)
                null
            }
        }
    }

    suspend fun deleteGlucoseInfo(command: DeleteGlucoseInfoCommand): HttpResponse {
        val response = client.post("$baseUrl/GlucoseInfo/Delete") {
            contentType(ContentType.Application.Json)
            setBody(command)
        }
        return response.also {
            if (response.status.isSuccess()) {
                Log.i("DiabetesApiClient", "Glucose info deleted successfully")
            } else {
                val error = parseErrorResponse(response)
                handleApiError(response.status, error)
            }
        }
    }

    @OptIn(InternalAPI::class)
    suspend fun recognizeText(bitmap: Bitmap): OcrResponse? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()

        val response = client.post("$baseUrl/VisionOcr/RecognizeText") {
            body = MultiPartFormDataContent(
                formData {
                    append("FormFile", byteArray, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"image.jpg\"")
                    })
                }
            )
        }

        return when {
            response.status.isSuccess() -> {
                val ocrResponse = Json.decodeFromString<OcrResponse>(response.bodyAsText())
                Log.i("DiabetesApiClient", "OCR recognized text successfully: $ocrResponse")
                ocrResponse
            }
            else -> {
                val error = parseErrorResponse(response)
                handleApiError(response.status, error)
                null
            }
        }
    }
}