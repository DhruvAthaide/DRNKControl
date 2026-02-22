package com.dhruvathaide.drnkcontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dhruvathaide.drnkcontrol.accessibility.GhostService
import com.dhruvathaide.drnkcontrol.ui.navigation.DRNKNavGraph
import com.dhruvathaide.drnkcontrol.ui.navigation.Screen
import com.dhruvathaide.drnkcontrol.ui.theme.DRNKControlTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if this is first launch OR if service is not yet enabled.
        // If either condition is true, show onboarding.
        val onboardingDone = getPreferences(MODE_PRIVATE)
            .getBoolean("onboarding_done", false)
        val serviceEnabled = GhostService.isEnabled(this)

        val startDestination = if (!onboardingDone || !serviceEnabled) {
            Screen.Onboarding.route
        } else {
            Screen.Dashboard.route
        }

        setContent {
            DRNKControlTheme {
                DRNKNavGraph(startDestination = startDestination)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Mark onboarding as done once user has seen it and service is active
        if (GhostService.isEnabled(this)) {
            getPreferences(MODE_PRIVATE).edit()
                .putBoolean("onboarding_done", true)
                .apply()
        }
    }
}