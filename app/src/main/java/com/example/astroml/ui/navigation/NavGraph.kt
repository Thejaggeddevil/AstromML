package com.example.astroml.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.astroml.data.models.Screen
import com.example.astroml.ui.screens.CompatibilityScreen
import com.example.astroml.ui.screens.HomeScreen
import com.example.astroml.ui.screens.HoroscopeScreen
import com.example.astroml.ui.screens.MuhuratScreen
import com.example.astroml.ui.screens.PalmScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) +
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(300)
                    )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) +
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(300)
                    )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) +
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(300)
                    )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) +
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(300)
                    )
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                isDarkMode = isDarkMode,
                onToggleTheme = onToggleTheme
            )
        }

        composable(Screen.Palm.route) {
            PalmScreen(
                isDarkMode = isDarkMode,
                onToggleTheme = onToggleTheme
            )
        }

        composable(Screen.Horoscope.route) {
            HoroscopeScreen(
                isDarkMode = isDarkMode,
                onToggleTheme = onToggleTheme
            )
        }

        composable(Screen.Compatibility.route) {
            CompatibilityScreen(
                isDarkMode = isDarkMode,
                onToggleTheme = onToggleTheme
            )
        }

        composable(Screen.Muhurat.route) {
            MuhuratScreen(
                isDarkMode = isDarkMode,
                onToggleTheme = onToggleTheme
            )
        }
    }
}