package com.dhruvathaide.drnkcontrol.ui.selector

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruvathaide.drnkcontrol.data.BlockedAppEntity
import com.dhruvathaide.drnkcontrol.domain.SafetyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class AppInfo(
    val packageName: String,
    val name: String,
    val isBlocked: Boolean,
    val icon: Drawable? = null
)

@HiltViewModel
class AppSelectorViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SafetyRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Combined flow that merges installed apps, their blocked status from DB, and the search query
    val filteredApps: Flow<List<AppInfo>> = combine(
        _installedApps,
        repository.getAllBlockedApps(),
        _searchQuery
    ) { installed, blocked, query ->
        val blockedMap = blocked.associateBy { it.packageName }
        installed.map { 
            it.copy(isBlocked = blockedMap.containsKey(it.packageName))
        }.filter {
            it.name.contains(query, ignoreCase = true) || it.packageName.contains(query, ignoreCase = true)
        }.sortedBy { it.name }
    }

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            val apps = withContext(Dispatchers.IO) {
                val pm = context.packageManager
                // Query all installed apps
                pm.getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter { app ->
                        // Filter out own app and extremely specific system services
                        app.packageName != context.packageName &&
                        !app.packageName.contains("systemui") &&
                        !app.packageName.contains("launcher")
                    }
                    .map { app ->
                        AppInfo(
                            packageName = app.packageName,
                            name = pm.getApplicationLabel(app).toString(),
                            isBlocked = false,
                            icon = try { pm.getApplicationIcon(app) } catch (e: Exception) { null }
                        )
                    }
                    .sortedBy { it.name }
            }
            _installedApps.value = apps
            _isLoading.value = false
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleAppBlock(app: AppInfo) {
        viewModelScope.launch {
            repository.toggleAppBlock(app.packageName, app.name, !app.isBlocked)
        }
    }
}
