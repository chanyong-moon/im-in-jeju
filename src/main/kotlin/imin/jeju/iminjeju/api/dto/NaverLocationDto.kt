package imin.jeju.iminjeju.api.dto

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
