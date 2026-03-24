package com.example.astroml.data.models

// ── Horoscope ──────────────────────────────────────────────────────────────────

data class HoroscopeRequest(
    val birth_date: String,
    val birth_time: String,
    val birth_city: String
)

data class HoroscopeResponse(
    val status: String = "",
    val sun_sign: String = "",
    val moon_sign: String = "",                            // ✅ Added
    val birth_date: String = "",
    val birth_time: String = "",
    val birth_city: String = "",
    val age: Int = 0,                                      // ✅ Added
    val element: String = "",                              // ✅ Added
    val horoscope: String = "",
    val sections: Map<String, String> = emptyMap(),
    val future_predictions: Map<String, String> = emptyMap(),
    val career_guidance: Map<String, String> = emptyMap() // ✅ Added
    // ❌ REMOVED kb_used, kb_context — backend never sends these → Gson crash
)

// ── Cities & Zodiac ────────────────────────────────────────────────────────────

data class CitiesResponse(
    val status: String = "",
    val cities: List<String> = emptyList(),
    val total: Int = 0
)

data class ZodiacResponse(
    val status: String = "",
    val signs: List<String> = emptyList(),
    val total: Int = 0
)

// ── Health ─────────────────────────────────────────────────────────────────────

data class HealthResponse(
    val status: String = "",
    val version: String = ""
    // ❌ REMOVED service, kb_available — not in backend response
)

// ── Batch Horoscope ────────────────────────────────────────────────────────────

data class BatchHoroscopeRequest(
    val requests: List<HoroscopeRequest>
)

data class BatchHoroscopeResult(
    val status: String = "",
    val birth_city: String = "",
    val birth_date: String = "",
    val sun_sign: String? = null,
    val horoscope: String? = null,
    val kb_used: Boolean? = null,
    val error: String? = null
)

data class BatchHoroscopeResponse(
    val status: String = "",
    val total_requested: Int = 0,
    val total_successful: Int = 0,
    val total_failed: Int = 0,
    val results: List<BatchHoroscopeResult> = emptyList()
)

// ── Palm ───────────────────────────────────────────────────────────────────────

data class PalmPrediction(
    val `class`: String = "",
    val confidence: Float = 0f,
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 0f,
    val height: Float = 0f
)

data class ConfidenceSummary(                              // ✅ Added
    val line: String = "",
    val confidence_percent: Float = 0f,
    val strength: String = ""
)

data class PalmAnalysisResponse(
    val status: String = "",
    val detected_lines: List<String> = emptyList(),
    val confidence_summary: List<ConfidenceSummary> = emptyList(), // ✅ Added
    val sections: Map<String, String> = emptyMap(),                // ✅ Added — 7 rich analysis sections
    val interpretation: String = "",
    val raw_predictions: List<PalmPrediction> = emptyList()        // ✅ Fixed — was List<String>
)

// ── Compatibility ──────────────────────────────────────────────────────────────

// ✅ FIXED: Backend expects nested person objects, not flat fields
data class PersonDetail(
    val name: String,
    val birth_date: String,
    val birth_time: String,
    val birth_city: String
)

data class CompatibilityRequest(
    val person1: PersonDetail,
    val person2: PersonDetail
)

data class ElementCompatibility(
    val person1_element: String = "",
    val person2_element: String = "",
    val compatible: Boolean = false
)

data class AuspiciousDate(
    val date: String = "",
    val day: String = "",
    val time: String = "",
    val time_type: String = ""
)

// ✅ FIXED: Completely rewritten to match actual backend response
data class CompatibilityResponse(
    val status: String = "",
    val person1_name: String = "",
    val person2_name: String = "",
    val person1_sign: String = "",
    val person2_sign: String = "",
    val compatibility_score: Int = 0,
    val quality: String = "",
    val description: String = "",
    val strengths: String = "",
    val challenges: String = "",
    val marriage_timeline: String = "",
    val element_compatibility: ElementCompatibility = ElementCompatibility(),
    val lucky_months: List<String> = emptyList(),
    val auspicious_dates: List<AuspiciousDate> = emptyList()
)

// ── Muhurat ────────────────────────────────────────────────────────────────────

// ✅ FIXED: Backend expects POST body {zodiac_sign, activity_type}
data class MuhuratRequest(
    val zodiac_sign: String,
    val activity_type: String
)

data class MuhuratResult(
    val date: String = "",
    val date_short: String = "",
    val day: String = "",
    val time: String = "",
    val time_range: String = "",
    val score: Int = 0
)

data class MuhuratResponse(
    val status: String = "",
    val zodiac_sign: String = "",          // ✅ Fixed: was "sign"
    val activity_type: String = "",        // ✅ Fixed: was "activity"
    val activity_description: String = "",
    val auspicious_hours: String = "",
    val muhurats: List<MuhuratResult> = emptyList()
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
    object Home          : Screen("home")
    object Palm          : Screen("palm")
    object Horoscope     : Screen("horoscope")
    object Compatibility : Screen("compatibility")
    object Muhurat       : Screen("muhurat")
}

data class ThemePreference(
    val isDarkMode: Boolean = false
)