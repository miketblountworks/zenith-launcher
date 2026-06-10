package com.example.model

data class WidgetData(
    val id: Int,
    val widthSpan: Int = 4,
    val heightSpan: Int = 2,
    val pageName: String = "",
    val revision: Int = 0
)
