package com.example.model

data class LauncherUiState(
    val apps: List<AppInfo> = emptyList(),
    val letters: List<Char> = emptyList(),
    val isLoading: Boolean = true
)
