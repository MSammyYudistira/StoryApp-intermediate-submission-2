package com.example.storyapp.data

import com.example.storyapp.data.remote.dto.request.LoginRequest
import com.example.storyapp.data.remote.dto.request.SignUpRequest
import com.example.storyapp.data.remote.dto.response.DetailResponseDto
import com.example.storyapp.data.remote.dto.response.LocationStoryResponseDto
import com.example.storyapp.data.remote.dto.response.LoginResponseDto
import com.example.storyapp.data.remote.dto.response.StoryPagingResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    suspend fun register(@Body requestSignup: SignUpRequest): Response<DetailResponseDto>

    @POST("login")
    suspend fun login(@Body requestLogin: LoginRequest): Response<LoginResponseDto>

    @GET("stories")
    suspend fun getLocationStory(
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 0,
        @Header("Authorization") token: String,
    ): Response<LocationStoryResponseDto>

    @GET("stories")
    suspend fun getAllStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 0,
        @Header("Authorization") token: String,
    ): StoryPagingResponseDto

    @Multipart
    @POST("stories")
    suspend fun upload(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
        @Header("Authorization") token: String
    ): Response<DetailResponseDto>
}