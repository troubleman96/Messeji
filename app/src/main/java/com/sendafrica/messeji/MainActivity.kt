package com.sendafrica.messeji

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.sendafrica.messeji.data.AppSettings
import com.sendafrica.messeji.ui.MainViewModel
import com.sendafrica.messeji.ui.navigation.MessejiNavGraph
import com.sendafrica.messeji.ui.theme.MessejiTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appSettings: AppSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val preferences by appSettings.preferences.collectAsState(
                initial = com.sendafrica.messeji.data.AppPreferences()
            )
            val isLocked by viewModel.isLocked.collectAsState()

            MessejiTheme(
                darkTheme = when (preferences.themeMode) {
                    "dark" -> true
                    "light" -> false
                    else -> androidx.compose.foundation.isSystemInDarkTheme()
                }
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val startDest = if (preferences.onboardingComplete) "home" else "onboarding"

                    MessejiNavGraph(
                        navController = navController,
                        startDestination = startDest,
                        isLocked = isLocked && preferences.onboardingComplete && preferences.lockEnabled
                    )
                }
            }
        }
    }
}
