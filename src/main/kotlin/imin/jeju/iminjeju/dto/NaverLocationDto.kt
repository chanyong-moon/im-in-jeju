package imin.jeju.iminjeju.dto

import kotlinx.serialization.Serializable

@Serializable
data class NaverLocationDto(
    val items: List<Item>,

)

@Serializable
data class Item(
    val title: String,
    val category: String,
)
