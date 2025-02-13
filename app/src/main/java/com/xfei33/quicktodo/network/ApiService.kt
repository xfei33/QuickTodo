package com.xfei33.quicktodo.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class AuthRequest(val username: String, val password: String)
data class AuthResponse(val token: String)

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): Response<Void>

    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>
}
