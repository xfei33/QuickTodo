package com.xfei33.quicktodo.network

import androidx.room.TypeConverters
import com.xfei33.quicktodo.data.converter.LocalDateTimeConverter
import com.xfei33.quicktodo.model.Todo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import java.time.LocalDateTime

data class AuthRequest(val username: String, val password: String)
data class AuthResponse(val token: String, val userId: Long)

@TypeConverters(LocalDateTimeConverter::class)
interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): Response<Void>

    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @GET("todos/sync")
    suspend fun getIncrementalData(
        @Header("Authorization") token: String,
        @Query ("userId") userId: Long,
        @Query("lastSyncTime") lastSyncTime: LocalDateTime
    ): Response<List<Todo>>

    @POST("todos/sync")
    suspend fun uploadIncrementalData(
        @Header("Authorization") token: String,
        @Query("userId") userId: Long,
        @Body incrementalData: List<Todo>
    ): Response<Void>

    @GET("utils/latestTime")
    suspend fun getLatestTime(
        @Header("Authorization") token: String,
        @Query("userId") userId: Long
    ): Response<LocalDateTime>
}
