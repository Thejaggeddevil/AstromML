package com.example.astroml.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 52.dp, bottom = 24.dp)
    ) {
        // Top Bar
        HomeTopBar(isDarkMode = isDarkMode, onToggleTheme = onToggleTheme)

        Spacer(modifier = Modifier.height(24.dp))

        // Greeting
        GreetingSection()

        Spacer(modifier = Modifier.height(20.dp))

        // Typewriter Energy Card
        EnergyCard()

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Info Cards
        QuickInfoRow()
    }
}

@Composable
fun HomeTopBar(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌿", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "AstroML",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }

        // Theme Toggle Button
        IconButton(
            onClick = onToggleTheme,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Default.LightMode
                else Icons.Default.DarkMode,
                contentDescription = "Toggle Theme",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun GreetingSection() {
    Column {
        Text(
            text = "Namaste 🙏",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Welcome to AstroML",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EnergyCard() {
    // Typewriter messages
    val messages = listOf(
        "The stars align in your favor today ✨",
        "Trust your intuition — it guides you well 🌙",
        "A peaceful mind attracts beautiful things 🌿",
        "New beginnings are on their way to you 🌅",
        "Your inner light shines brighter each day 💫",
        "Patience and calm bring great rewards today 🍃",
    )

    var messageIndex by remember { mutableStateOf(0) }
    var displayedText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(true) }

    // Typewriter effect
    LaunchedEffect(messageIndex) {
        val fullText = messages[messageIndex]
        displayedText = ""
        isTyping = true

        // Type in
        for (char in fullText) {
            displayedText += char
            delay(45)
        }

        // Wait
        delay(2500)

        // Type out
        while (displayedText.isNotEmpty()) {
            displayedText = displayedText.dropLast(1)
            delay(25)
        }

        isTyping = false
        delay(300)
        messageIndex = (messageIndex + 1) % messages.size
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Today's Energy",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Typewriter text
            Box(modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(
                    text = displayedText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
fun QuickInfoRow() {
    val items = listOf(
        Triple("🤲", "Palm Reading", "Analyze your palm lines"),
        Triple("🔮", "Horoscope", "Get your birth chart"),
        Triple("💕", "Compatibility", "Check your match"),
        Triple("📅", "Muhurat", "Find auspicious dates"),
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { (icon, title, subtitle) ->
                    QuickInfoCard(
                        icon = icon,
                        title = title,
                        subtitle = subtitle,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun QuickInfoCard(
    icon: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}