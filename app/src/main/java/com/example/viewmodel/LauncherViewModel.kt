package com.example.viewmodel

import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AppInfo
import com.example.model.LauncherUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LauncherViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LauncherUiState())
    val uiState: StateFlow<LauncherUiState> = _uiState

    fun loadApps(packageManager: PackageManager) {
        viewModelScope.launch {
            val appList = withContext(Dispatchers.IO) {
                val intent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                val resolveInfos = packageManager.queryIntentActivities(intent, 0)
                resolveInfos.map {
                    AppInfo(
                        label = it.loadLabel(packageManager).toString(),
                        packageName = it.activityInfo.packageName,
                        icon = it.loadIcon(packageManager)
                    )
                }.sortedBy { it.label.lowercase() }.distinctBy { it.packageName }
            }
            val lettersList = appList.map { it.label.firstOrNull()?.uppercaseChar() ?: '#' }
                .distinct()
                .sorted()
                
            _uiState.value = LauncherUiState(
                apps = appList,
                letters = lettersList,
                isLoading = false
            )
        }
    }
}
