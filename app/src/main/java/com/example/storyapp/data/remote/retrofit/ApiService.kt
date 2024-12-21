package com.example.storyapp.data.remote.retrofit

import com.example.storyapp.data.remote.response.DetailResponse
import com.example.storyapp.data.remote.response.LocationStoryResponse
import com.example.storyapp.data.remote.response.LoginData
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.SignupData
import com.example.storyapp.data.remote.response.StoryPagingResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    fun userSignup(@Body requestSignup: SignupData): Call<DetailResponse>

    @POST("login")
    fun userLogin(@Body requestLogin: LoginData): Call<LoginResponse>

    @GET("stories")
    fun getLocationStory(
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 0,
        @Header("Authorization") token: String,
    ): Call<LocationStoryResponse>

    @GET("stories")
    fun getAllStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 0,
        @Header("Authorization") token: String,
    ): StoryPagingResponse

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
        @Header("Authorization") token: String
    ): Call<DetailResponse>
}