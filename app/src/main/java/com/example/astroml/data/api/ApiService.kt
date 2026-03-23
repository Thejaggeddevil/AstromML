package com.example.astroml.data.api

import com.example.astroml.data.models.BatchHoroscopeResponse
import com.example.astroml.data.models.CitiesResponse
import com.example.astroml.data.models.CompatibilityRequest
import com.example.astroml.data.models.CompatibilityResponse
import com.example.astroml.data.models.HealthResponse
import com.example.astroml.data.models.HoroscopeRequest
import com.example.astroml.data.models.HoroscopeResponse
import com.example.astroml.data.models.MuhuratResponse
import com.example.astroml.data.models.PalmAnalysisResponse
import com.example.astroml.data.models.ZodiacResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @GET("health")
    suspend fun healthCheck(): Response<HealthResponse>

    @GET("cities")
    suspend fun getCities(): Response<CitiesResponse>

    @GET("zodiac-signs")
    suspend fun getZodiacSigns(): Response<ZodiacResponse>

    @POST("generate-horoscope")
    suspend fun generateHoroscope(
        @Body request: HoroscopeRequest
    ): Response<HoroscopeResponse>

    @POST("batch-horoscopes")
    suspend fun generateBatchHoroscopes(
        @Body requests: List<HoroscopeRequest>
    ): Response<BatchHoroscopeResponse>

    @Multipart
    @POST("analyze-palm")
    suspend fun analyzePalm(
        @Part image: MultipartBody.Part
    ): Response<PalmAnalysisResponse>

    @POST("check-compatibility")
    suspend fun checkCompatibility(
        @Body request: CompatibilityRequest
    ): Response<CompatibilityResponse>

    @GET("muhurat")
    suspend fun getMuhurat(
        @Query("sign") sign: String,
        @Query("activity") activity: String
    ): Response<MuhuratResponse>
}

