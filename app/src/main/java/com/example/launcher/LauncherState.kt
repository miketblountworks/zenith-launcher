package com.example.launcher

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.WidgetData

object LauncherState {
    val _longPressedWidgetId = MutableStateFlow<Int?>(null)
    val draggingWidgetId = MutableStateFlow<Int?>(null)
    val dragOffsetX = MutableStateFlow(0f)
    val dragOffsetY = MutableStateFlow(0f)
    
    val _widgetDataList = MutableStateFlow<List<WidgetData>>(emptyList())
    val widgetDataList: StateFlow<List<WidgetData>> get() = _widgetDataList
    val widgetTargetPage = MutableStateFlow<String>("")

    val activePages = MutableStateFlow<List<String>>(listOf("App List", "Music", "Notifications"))
}
