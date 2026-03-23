package com.example.astroml.data.models

// ── Horoscope ──────────────────────────────────────────────────────────────────

data class HoroscopeRequest(
    val birth_date: String,
    val birth_time: String,
    val birth_city: String
)

data class HoroscopeResponse(
    val status: String,
    val sun_sign: String,
    val birth_date: String,
    val birth_time: String,
    val birth_city: String,
    val horoscope: String,
    val sections: Map<String, String>,
    val future_predictions: Map<String, String>,
    val kb_used: Boolean,
    val kb_context: List<String>
)

// ── Cities & Zodiac ────────────────────────────────────────────────────────────

data class CitiesResponse(
    val status: String,
    val cities: List<String>,
    val total: Int
)

data class ZodiacResponse(
    val status: String,
    val signs: List<String>,
    val total: Int
)

// ── Health ─────────────────────────────────────────────────────────────────────

data class HealthResponse(
    val status: String,
    val service: String,
    val version: String,
    val kb_available: Boolean
)

// ── Batch Horoscope ────────────────────────────────────────────────────────────

data class BatchHoroscopeRequest(
    val requests: List<HoroscopeRequest>
)

data class BatchHoroscopeResult(
    val status: String,
    val birth_city: String,
    val birth_date: String,
    val sun_sign: String?,
    val horoscope: String?,
    val kb_used: Boolean?,
    val error: String?
)

data class BatchHoroscopeResponse(
    val status: String,
    val total_requested: Int,
    val total_successful: Int,
    val total_failed: Int,
    val results: List<BatchHoroscopeResult>
)

// ── Palm ───────────────────────────────────────────────────────────────────────

data class PalmPrediction(
    val `class`: String,
    val confidence: Float,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)

data class PalmAnalysisResponse(
    val status: String,
    val detected_lines: List<String>,
    val interpretation: String,
    val raw_predictions: List<String>
)

// ── Compatibility ──────────────────────────────────────────────────────────────

data class CompatibilityRequest(
    val person1_name: String,
    val person1_birth_date: String,
    val person1_birth_time: String,
    val person1_city: String,
    val person2_name: String,
    val person2_birth_date: String,
    val person2_birth_time: String,
    val person2_city: String
)

data class PersonInfo(
    val name: String,
    val sun_sign: String,
    val age: Int
)

data class ZodiacCompatibility(
    val score: Int,
    val insights: String
)

data class MarriagePrediction(
    val quality: String,
    val description: String,
    val timeline: String,
    val strengths: String,
    val challenges: String,
    val score: Int,
    val auspicious_months: List<String>
)

data class CompatibilityResponse(
    val status: String,
    val person1: PersonInfo,
    val person2: PersonInfo,
    val zodiac_compatibility: ZodiacCompatibility,
    val marriage_prediction: MarriagePrediction
)

// ── Muhurat ────────────────────────────────────────────────────────────────────

data class MuhuratResult(
    val date: String,
    val day_name: String,      // "dayName" tha pehle — backend "day_name" bhejta hai
    val time: String,
    val time_range: String,    // "timeRange" tha pehle — backend "time_range" bhejta hai
    val score: Int,
    val activity: String
)

data class MuhuratResponse(
    val status: String,
    val sign: String,
    val activity: String,
    val muhurats: List<MuhuratResult>
)

// ── UI State ───────────────────────────────────────────────────────────────────

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// ── Navigation ─────────────────────────────────────────────────────────────────

sealed class Screen(val route: String) {
    object Home        : Screen("home")
    object Palm        : Screen("palm")
    object Horoscope   : Screen("horoscope")
    object Compatibility : Screen("compatibility")
    object Muhurat     : Screen("muhurat")
}

data class ThemePreference(
    val isDarkMode: Boolean = false
)