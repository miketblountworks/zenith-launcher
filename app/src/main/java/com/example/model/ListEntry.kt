package com.example.model

sealed class ListEntry {
    data class Header(val letter: Char) : ListEntry()
    data class App(val appInfo: AppInfo) : ListEntry()
}
