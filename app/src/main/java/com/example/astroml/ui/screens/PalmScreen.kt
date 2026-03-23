package com.example.astroml.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.astroml.data.api.RetrofitClient
import com.example.astroml.data.models.PalmAnalysisResponse
import com.example.astroml.data.models.UiState
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
fun PalmScreen(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    val scrollState  = rememberScrollState()
    val context      = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var palmState by remember {
        mutableStateOf<UiState<PalmAnalysisResponse>>(UiState.Idle)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            palmState = UiState.Idle
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 52.dp, bottom = 24.dp)
    ) {

        // ── Top Bar ────────────────────────────────────────────────────────────
        PalmTopBar(isDarkMode = isDarkMode, onToggleTheme = onToggleTheme)

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Upload your palm photo for a detailed reading",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Upload Card ────────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { imagePickerLauncher.launch("image/*") },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(0.dp),
            border = BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline
            )
        ) {
            if (selectedImageUri != null) {
                Box {
                    AsyncImage(
                        model          = selectedImageUri,
                        contentDescription = "Palm Image",
                        modifier       = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale   = ContentScale.Crop
                    )
                    // "Tap to change" overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(14.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text       = "📸 Tap to change",
                            style      = MaterialTheme.typography.labelSmall,
                            color      = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "🤲", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text       = "Tap to upload palm photo",
                        style      = MaterialTheme.typography.titleSmall,
                        color      = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text  = "Choose from Gallery",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Analyze Button ─────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = selectedImageUri != null,
            enter   = fadeIn() + slideInVertically(),
            exit    = fadeOut() + slideOutVertically()
        ) {
            Button(
                onClick = {
                    selectedImageUri?.let { uri ->
                        coroutineScope.launch {
                            palmState = UiState.Loading
                            try {
                                // Read image bytes
                                val bytes = context.contentResolver
                                    .openInputStream(uri)
                                    ?.readBytes()
                                    ?: throw Exception("Could not read image")

                                val requestBody = bytes.toRequestBody(
                                    "image/jpeg".toMediaTypeOrNull()
                                )
                                // "file" — backend expects this field name
                                val part = MultipartBody.Part.createFormData(
                                    "file", "palm.jpg", requestBody
                                )

                                val response = RetrofitClient.apiService
                                    .analyzePalm(part)

                                if (response.isSuccessful && response.body() != null) {
                                    palmState = UiState.Success(response.body()!!)
                                } else {
                                    palmState = UiState.Error(
                                        "Could not analyze palm (${response.code()}). " +
                                                "Try a clearer image."
                                    )
                                }
                            } catch (e: Exception) {
                                palmState = UiState.Error(
                                    "Network error: ${e.message}"
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = palmState !is UiState.Loading
            ) {
                when (palmState) {
                    is UiState.Loading -> {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(22.dp),
                            color       = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text  = "Reading your palm...",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    else -> {
                        Text(
                            text       = "Analyze Palm 🔍",
                            style      = MaterialTheme.typography.titleSmall,
                            color      = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Result / Error ─────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = palmState is UiState.Success || palmState is UiState.Error,
            enter   = fadeIn() + expandVertically(),
            exit    = fadeOut() + shrinkVertically()
        ) {
            when (val state = palmState) {
                is UiState.Success -> PalmResultCard(data = state.data)
                is UiState.Error   -> PalmErrorCard(message = state.message)
                else               -> {}
            }
        }

        // ── Idle info cards ────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = palmState is UiState.Idle,
            enter   = fadeIn(),
            exit    = fadeOut()
        ) {
            Column {
                // What we detect
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(20.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border    = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text       = "What we detect",
                            style      = MaterialTheme.typography.titleSmall,
                            color      = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PalmLineItem(
                            emoji = "❤️",
                            name  = "Heart Line",
                            desc  = "Emotional nature & relationships"
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        PalmLineItem(
                            emoji = "🧠",
                            name  = "Head Line",
                            desc  = "Intellect & thinking patterns"
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        PalmLineItem(
                            emoji = "🌱",
                            name  = "Life Line",
                            desc  = "Vitality & life journey"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tips
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(20.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text       = "📌 Tips for best results",
                            style      = MaterialTheme.typography.titleSmall,
                            color      = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        TipItem(text = "Use good lighting — natural light works best")
                        TipItem(text = "Keep your palm flat and fully open")
                        TipItem(text = "Hold camera steady, avoid blur")
                        TipItem(text = "Use your dominant hand for reading")
                    }
                }
            }
        }
    }
}

// ── Palm Result Card ───────────────────────────────────────────────────────────

@Composable
fun PalmResultCard(data: PalmAnalysisResponse) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(24.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        border    = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🌿", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text       = "Palm Analysis Complete",
                        style      = MaterialTheme.typography.titleMedium,
                        color      = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text  = "Here's what your palm reveals",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Detected Lines
            if (data.detected_lines.isNotEmpty()) {
                Text(
                    text       = "Lines Detected",
                    style      = MaterialTheme.typography.labelLarge,
                    color      = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))

                data.detected_lines.forEach { line ->
                    val emoji = when {
                        line.contains("heart", ignoreCase = true) -> "❤️"
                        line.contains("head",  ignoreCase = true) -> "🧠"
                        line.contains("life",  ignoreCase = true) -> "🌱"
                        else -> "✨"
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = emoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text       = line,
                            style      = MaterialTheme.typography.bodyMedium,
                            color      = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Card(
                    shape  = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⚠️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text  = "No clear palm lines detected. Try a clearer photo with better lighting.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Interpretation
            if (data.interpretation.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✨", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text       = "Your Reading",
                        style      = MaterialTheme.typography.titleSmall,
                        color      = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text       = data.interpretation,
                        modifier   = Modifier.padding(16.dp),
                        style      = MaterialTheme.typography.bodyMedium,
                        color      = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}

// ── Error Card ─────────────────────────────────────────────────────────────────

@Composable
fun PalmErrorCard(message: String) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "⚠️", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text  = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

// ── Top Bar ────────────────────────────────────────────────────────────────────

@Composable
fun PalmTopBar(isDarkMode: Boolean, onToggleTheme: () -> Unit) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text       = "🤲 Palm Reading",
                style      = MaterialTheme.typography.titleLarge,
                color      = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }
        IconButton(
            onClick  = onToggleTheme,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector    = if (isDarkMode) Icons.Default.LightMode
                else Icons.Default.DarkMode,
                contentDescription = "Toggle Theme",
                tint           = MaterialTheme.colorScheme.primary,
                modifier       = Modifier.size(20.dp)
            )
        }
    }
}

// ── Palm Line Item ─────────────────────────────────────────────────────────────

@Composable
fun PalmLineItem(emoji: String, name: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text       = name,
                style      = MaterialTheme.typography.bodyMedium,
                color      = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text  = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Tip Item ───────────────────────────────────────────────────────────────────

@Composable
fun TipItem(text: String) {
    Row(
        modifier          = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text       = "•",
            style      = MaterialTheme.typography.bodyMedium,
            color      = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text  = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}