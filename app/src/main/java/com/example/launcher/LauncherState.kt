package com.example.launcher

import com.example.model.WidgetData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object LauncherState {
    val _longPressedWidgetId = MutableStateFlow<Int?>(null)
    val draggingWidgetId = MutableStateFlow<Int?>(null)
    val dragOffsetX = MutableStateFlow(0f)
    val dragOffsetY = MutableStateFlow(0f)

    val _widgetDataList = MutableStateFlow<List<WidgetData>>(emptyList())
    val widgetDataList: StateFlow<List<WidgetData>> = _widgetDataList
    val widgetTargetPage = MutableStateFlow("App List")

    val activePages = MutableStateFlow(listOf("App List"))
}
