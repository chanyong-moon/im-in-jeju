package imin.jeju.iminjeju.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KakaoLocationDto(
    val documents: List<Document>
)

@Serializable
data class Document(
    val id: String,
    @SerialName("place_name") val name: String,
    @SerialName("category_name") val categoryName: String,
)
