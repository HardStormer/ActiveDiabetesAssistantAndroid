package ru.guzeevmd.activediabetesassistant.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListResponse<T>(
    val modelList: Collection<T>,
    val totalCount: Int
)

@Serializable
data class CheckTokenQuery(
    val token: String,
)

@Serializable
data class AskAiCommand(
    val prompt: String,
    val systemPrompt: String?
)
@Serializable
data class CreateGlucoseInfoCommand(
    val glucoseData: Float,
    val stepsCount: Int?
)

@Serializable
data class UpdateGlucoseInfoCommand(
    val glucoseInfoId: String,
    val glucoseData: Float,
    val stepsCount: Int?
)

@Serializable
data class DeleteGlucoseInfoCommand(
    val soft: Boolean,
    val glucoseInfoId: String
)

@Serializable
data class GlucoseInfoViewModel(
    val id: String,
    val createdAt: String,
    val glucoseData: Float,
    val stepsCount: Int?
)

@Serializable
data class CreateMyPersonInfoCommand(
    val name: String?,
    val age: Int,
    val sex: Int,
    val diabetesType: Int
)

@Serializable
data class UpdateMyPersonInfoCommand(
    val name: String?,
    val age: Int,
    val sex: Int,
    val diabetesType: Int
)

@Serializable
data class DeleteMyPersonInfoCommand(
    val soft: Boolean
)

@Serializable
data class PersonInfoViewModel(
    @SerialName("id")
    val id: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("name")
    val name: String?,
    @SerialName("age")
    val age: Int,
    @SerialName("sex")
    val sex: Int,
    @SerialName("diabetesType")
    val diabetesType: Int
)

@Serializable
data class LoginUserCommand(
    val email: String?,
    val password: String?
)

@Serializable
data class LoginUserCommandResponse(
    val token: String?
)

@Serializable
data class RegisterUserCommand(
    val email: String?,
    val password: String?
)

@Serializable
data class RegisterUserCommandResponse(
    val token: String?
)

@Serializable
data class UserViewModel(
    val id: String,
    val createdAt: String,
    val email: String?,
    val bio: String?
)

@Serializable
data class UpdateUserEmailCommandRequest(
    val email: String?
)

@Serializable
data class UpdateUserPasswordCommandRequest(
    val oldPassword: String?,
    val password: String?
)

@Serializable
data class ProblemDetails(
    val type: String?,
    val title: String?,
    val status: Int?,
    val detail: String?,
    val instance: String?
)

@Serializable
data class ValidationProblemDetails(
    val type: String?,
    val title: String?,
    val status: Int?,
    val detail: String?,
    val instance: String?,
    val errors: Map<String, List<String>>?
)

@Serializable
enum class Sex(val value: Int) {
    Male(0),
    Female(1);
    companion object {
        fun fromInt(value: Int): Sex {
            return values().firstOrNull { it.value == value } ?: Male
        }
    }
}
@Serializable
data class OcrResponse(
    val text: String,
    val bestBlock: String,
    val biggestBlockText: String,
    val mostSquareBlockText: String
)

@Serializable
enum class DiabetesType(val value: Int) {
    Type1(0),
    Type2(1);
    companion object {
        fun fromInt(value: Int): DiabetesType {
            return values().firstOrNull { it.value == value } ?: Type1
        }
    }
}