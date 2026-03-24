package com.example.astroml.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astroml.data.api.RetrofitClient
import com.example.astroml.data.models.CompatibilityRequest
import com.example.astroml.data.models.CompatibilityResponse
import com.example.astroml.data.models.PersonDetail          // ✅ New nested model
import com.example.astroml.data.models.UiState
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompatibilityScreen(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    val scrollState    = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Person 1
    var name1 by remember { mutableStateOf("") }
    var date1 by remember { mutableStateOf("") }
    var time1 by remember { mutableStateOf("") }
    var city1 by remember { mutableStateOf("") }
    var city1Expanded by remember { mutableStateOf(false) }

    // Person 2
    var name2 by remember { mutableStateOf("") }
    var date2 by remember { mutableStateOf("") }
    var time2 by remember { mutableStateOf("") }
    var city2 by remember { mutableStateOf("") }
    var city2Expanded by remember { mutableStateOf(false) }

    var cities      by remember { mutableStateOf<List<String>>(emptyList()) }
    var compatState by remember { mutableStateOf<UiState<CompatibilityResponse>>(UiState.Idle) }

    var showDatePicker1 by remember { mutableStateOf(false) }
    var showDatePicker2 by remember { mutableStateOf(false) }
    var showTimePicker1 by remember { mutableStateOf(false) }
    var showTimePicker2 by remember { mutableStateOf(false) }

    val datePicker1State = rememberDatePickerState()
    val datePicker2State = rememberDatePickerState()
    val timePicker1State = rememberTimePickerState(is24Hour = true)
    val timePicker2State = rememberTimePickerState(is24Hour = true)

    fun millisToDate(millis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        val y   = cal.get(Calendar.YEAR)
        val m   = String.format("%02d", cal.get(Calendar.MONTH) + 1)
        val d   = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))
        return "$y-$m-$d"
    }

    if (showDatePicker1) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker1 = false },
            confirmButton = {
                TextButton(onClick = {
                    datePicker1State.selectedDateMillis?.let { date1 = millisToDate(it) }
                    showDatePicker1 = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker1 = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePicker1State) }
    }

    if (showDatePicker2) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker2 = false },
            confirmButton = {
                TextButton(onClick = {
                    datePicker2State.selectedDateMillis?.let { date2 = millisToDate(it) }
                    showDatePicker2 = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker2 = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePicker2State) }
    }

    if (showTimePicker1) {
        AlertDialog(
            onDismissRequest = { showTimePicker1 = false },
            confirmButton = {
                TextButton(onClick = {
                    time1 = String.format("%02d:%02d", timePicker1State.hour, timePicker1State.minute)
                    showTimePicker1 = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker1 = false }) { Text("Cancel") } },
            text = { TimePicker(state = timePicker1State) }
        )
    }

    if (showTimePicker2) {
        AlertDialog(
            onDismissRequest = { showTimePicker2 = false },
            confirmButton = {
                TextButton(onClick = {
                    time2 = String.format("%02d:%02d", timePicker2State.hour, timePicker2State.minute)
                    showTimePicker2 = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker2 = false }) { Text("Cancel") } },
            text = { TimePicker(state = timePicker2State) }
        )
    }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.apiService.getCities()
            if (response.isSuccessful) cities = response.body()?.cities ?: emptyList()
        } catch (e: Exception) {}
    }

    val isFormValid = name1.isNotEmpty() && date1.isNotEmpty() &&
            time1.isNotEmpty() && city1.isNotEmpty() &&
            name2.isNotEmpty() && date2.isNotEmpty() &&
            time2.isNotEmpty() && city2.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 52.dp, bottom = 24.dp)
    ) {
        // Top Bar
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = "💕 Compatibility",
                style      = MaterialTheme.typography.titleLarge,
                color      = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick  = onToggleTheme,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector        = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text       = "Match Check",
            style      = MaterialTheme.typography.headlineMedium,
            color      = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text  = "Enter birth details of both people",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        PersonInputCard(
            personNumber      = 1,
            name              = name1, onNameChange = { name1 = it },
            date              = date1, onDateClick  = { showDatePicker1 = true },
            time              = time1, onTimeClick  = { showTimePicker1 = true },
            city              = city1, onCityChange = { city1 = it },
            cityExpanded      = city1Expanded,
            onCityExpandedChange = { city1Expanded = it },
            cities            = cities
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
            Text(text = "  💕  ", fontSize = 20.sp)
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
        }

        Spacer(modifier = Modifier.height(16.dp))

        PersonInputCard(
            personNumber      = 2,
            name              = name2, onNameChange = { name2 = it },
            date              = date2, onDateClick  = { showDatePicker2 = true },
            time              = time2, onTimeClick  = { showTimePicker2 = true },
            city              = city2, onCityChange = { city2 = it },
            cityExpanded      = city2Expanded,
            onCityExpandedChange = { city2Expanded = it },
            cities            = cities
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    compatState = UiState.Loading
                    try {
                        // ✅ FIXED: was flat fields — backend needs nested PersonDetail objects
                        val response = RetrofitClient.apiService.checkCompatibility(
                            CompatibilityRequest(
                                person1 = PersonDetail(
                                    name       = name1,
                                    birth_date = date1,
                                    birth_time = time1,
                                    birth_city = city1
                                ),
                                person2 = PersonDetail(
                                    name       = name2,
                                    birth_date = date2,
                                    birth_time = time2,
                                    birth_city = city2
                                )
                            )
                        )
                        compatState = if (response.isSuccessful && response.body() != null) {
                            UiState.Success(response.body()!!)
                        } else {
                            UiState.Error("Could not check compatibility. Try again. (${response.code()})")
                        }
                    } catch (e: Exception) {
                        compatState = UiState.Error("Network error: ${e.message}")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape    = RoundedCornerShape(16.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled  = isFormValid && compatState !is UiState.Loading
        ) {
            when (compatState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(22.dp),
                        color       = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text  = "Checking...",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                else -> {
                    Text(
                        text       = "Check Compatibility 💕",
                        style      = MaterialTheme.typography.titleSmall,
                        color      = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        AnimatedVisibility(
            visible = compatState is UiState.Success || compatState is UiState.Error,
            enter   = fadeIn() + expandVertically(),
            exit    = fadeOut() + shrinkVertically()
        ) {
            when (val state = compatState) {
                is UiState.Success -> CompatibilityResultCard(data = state.data)
                is UiState.Error   -> ErrorCard(message = state.message)
                else               -> {}
            }
        }
    }
}

// ── Person Input Card ──────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonInputCard(
    personNumber: Int,
    name: String,  onNameChange: (String) -> Unit,
    date: String,  onDateClick: () -> Unit,
    time: String,  onTimeClick: () -> Unit,
    city: String,  onCityChange: (String) -> Unit,
    cityExpanded: Boolean,
    onCityExpandedChange: (Boolean) -> Unit,
    cities: List<String>
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        border    = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "$personNumber",
                        style      = MaterialTheme.typography.titleSmall,
                        color      = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text       = "Person $personNumber",
                    style      = MaterialTheme.typography.titleMedium,
                    color      = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value         = name,
                onValueChange = onNameChange,
                label         = { Text("Name", style = MaterialTheme.typography.bodyMedium) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value         = date,
                    onValueChange = {},
                    label         = { Text("Date", style = MaterialTheme.typography.bodyMedium) },
                    placeholder   = { Text("Pick date", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier      = Modifier.weight(1f),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    trailingIcon = {
                        IconButton(onClick = onDateClick) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date",
                                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                    },
                    readOnly   = true,
                    singleLine = true
                )
                OutlinedTextField(
                    value         = time,
                    onValueChange = {},
                    label         = { Text("Time", style = MaterialTheme.typography.bodyMedium) },
                    placeholder   = { Text("Pick time", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier      = Modifier.weight(1f),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    trailingIcon = {
                        IconButton(onClick = onTimeClick) {
                            Icon(Icons.Default.Schedule, contentDescription = "Pick Time",
                                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                    },
                    readOnly   = true,
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            ExposedDropdownMenuBox(expanded = cityExpanded, onExpandedChange = onCityExpandedChange) {
                OutlinedTextField(
                    value         = city,
                    onValueChange = onCityChange,
                    label         = { Text("Birth City", style = MaterialTheme.typography.bodyMedium) },
                    modifier      = Modifier.fillMaxWidth().menuAnchor(),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                    },
                    singleLine = true
                )
                val filtered = cities.filter { it.contains(city, ignoreCase = true) }
                if (filtered.isNotEmpty()) {
                    ExposedDropdownMenu(expanded = cityExpanded, onDismissRequest = { onCityExpandedChange(false) }) {
                        filtered.take(8).forEach { c ->
                            DropdownMenuItem(
                                text    = { Text(c, style = MaterialTheme.typography.bodyMedium) },
                                onClick = { onCityChange(c); onCityExpandedChange(false) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Compatibility Result Card ──────────────────────────────────────────────────

@Composable
fun CompatibilityResultCard(data: CompatibilityResponse) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier            = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Names & Signs ──────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) { Text(text = "👤", fontSize = 24.sp) }
                    Spacer(modifier = Modifier.height(6.dp))
                    // ✅ FIXED: was data.person1.name → now data.person1_name
                    Text(
                        text       = data.person1_name,
                        style      = MaterialTheme.typography.titleSmall,
                        color      = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                    // ✅ FIXED: was data.person1.sun_sign → now data.person1_sign
                    Text(
                        text  = data.person1_sign,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(text = "💕", fontSize = 28.sp)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) { Text(text = "👤", fontSize = 24.sp) }
                    Spacer(modifier = Modifier.height(6.dp))
                    // ✅ FIXED: was data.person2.name → now data.person2_name
                    Text(
                        text       = data.person2_name,
                        style      = MaterialTheme.typography.titleSmall,
                        color      = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                    // ✅ FIXED: was data.person2.sun_sign → now data.person2_sign
                    Text(
                        text  = data.person2_sign,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(16.dp))

            // ── Score ──────────────────────────────────────────────────────────
            Text(
                text       = "Compatibility Score",
                style      = MaterialTheme.typography.labelLarge,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            // ✅ FIXED: was data.zodiac_compatibility.score → now data.compatibility_score
            Text(
                text       = "${data.compatibility_score}%",
                style      = MaterialTheme.typography.displaySmall,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress   = { data.compatibility_score / 100f },
                modifier   = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color      = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(14.dp))
            // ✅ FIXED: was data.zodiac_compatibility.insights → now data.description
            Text(
                text      = data.description,
                style     = MaterialTheme.typography.bodySmall,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            // ── Marriage Prediction ────────────────────────────────────────────
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text       = "💍 Marriage Prediction",
                style      = MaterialTheme.typography.titleSmall,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border   = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // ✅ FIXED: was data.marriage_prediction.quality → now data.quality
                        Text(
                            text       = data.quality,
                            style      = MaterialTheme.typography.titleSmall,
                            color      = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        // ✅ FIXED: was data.marriage_prediction.score → now data.compatibility_score
                        Text(
                            text  = "Score: ${data.compatibility_score}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text       = data.description,
                        style      = MaterialTheme.typography.bodySmall,
                        color      = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                    // ✅ FIXED: was data.marriage_prediction.timeline → now data.marriage_timeline
                    if (data.marriage_timeline.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text       = "📅 Timeline: ${data.marriage_timeline}",
                            style      = MaterialTheme.typography.bodySmall,
                            color      = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    // ✅ FIXED: was data.marriage_prediction.strengths → now data.strengths
                    if (data.strengths.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text       = "✅ Strengths: ${data.strengths}",
                            style      = MaterialTheme.typography.bodySmall,
                            color      = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                    // ✅ FIXED: was data.marriage_prediction.auspicious_months → now data.lucky_months
                    if (data.lucky_months.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text       = "🌟 Best months: ${data.lucky_months.joinToString(", ")}",
                            style      = MaterialTheme.typography.bodySmall,
                            color      = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    // ✅ NEW: Also show auspicious dates if available
                    if (data.auspicious_dates.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text       = "📅 Auspicious Dates",
                            style      = MaterialTheme.typography.labelMedium,
                            color      = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        data.auspicious_dates.take(3).forEach { aDate ->
                            Text(
                                text       = "• ${aDate.date} (${aDate.day}) — ${aDate.time}",
                                style      = MaterialTheme.typography.bodySmall,
                                color      = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            // ── Element Compatibility ──────────────────────────────────────────
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border   = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "Elements: ${data.element_compatibility.person1_element} × ${data.element_compatibility.person2_element}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text       = if (data.element_compatibility.compatible) "✅ Compatible" else "⚡ Different",
                        style      = MaterialTheme.typography.labelSmall,
                        color      = if (data.element_compatibility.compatible) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ── Shared Error Card ──────────────────────────────────────────────────────────

//@Composable
//fun ErrorCard(message: String) {
//    Card(
//        modifier  = Modifier.fillMaxWidth(),
//        shape     = RoundedCornerShape(16.dp),
//        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
//    ) {
//        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
//            Text(text = "⚠️", fontSize = 24.sp)
//            Spacer(modifier = Modifier.width(12.dp))
//            Text(
//                text  = message,
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onErrorContainer
//            )
//        }
//    }
//}