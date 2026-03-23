package com.example.astroml

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.astroml.data.models.Screen
import com.example.astroml.ui.navigation.NavGraph
import com.example.astroml.ui.theme.AstroMLTheme
import com.example.astroml.ui.theme.SageGreen
import com.example.astroml.ui.theme.MochaBrown
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedVisibility

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by rememberSaveable { mutableStateOf(false) }

            AstroMLTheme(darkTheme = isDarkMode) {
                AstroMLApp(
                    isDarkMode = isDarkMode,
                    onToggleTheme = { isDarkMode = !isDarkMode }
                )
            }
        }
    }
}

@Composable
fun AstroMLApp(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AstroBottomNav(navController = navController, isDarkMode = isDarkMode)
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            isDarkMode = isDarkMode,
            onToggleTheme = onToggleTheme,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AstroBottomNav(
    navController: NavController,
    isDarkMode: Boolean
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        BottomNavItem(
            screen = Screen.Home,
            label = "Home",
            icon = "🏠"
        ),
        BottomNavItem(
            screen = Screen.Palm,
            label = "Palm",
            icon = "🤲"
        ),
        BottomNavItem(
            screen = Screen.Horoscope,
            label = "Rashi",
            icon = "🔮"
        ),
        BottomNavItem(
            screen = Screen.Compatibility,
            label = "Match",
            icon = "💕"
        ),
        BottomNavItem(
            screen = Screen.Muhurat,
            label = "Muhurat",
            icon = "📅"
        ),
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Text(
                        text = item.icon,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = if (isDarkMode) MochaBrown else SageGreen,
                    selectedTextColor = if (isDarkMode) MochaBrown else SageGreen,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: String
)