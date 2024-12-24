package com.example.storyapp.data.contract

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.example.storyapp.data.local.room.entity.StoryEntity
import com.example.storyapp.data.remote.dto.request.LoginRequest
import com.example.storyapp.data.remote.dto.request.SignUpRequest
import com.example.storyapp.data.remote.dto.response.LoginResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface RemoteDataRepository {
    val stories: LiveData<List<StoryEntity>>
    val message: LiveData<String>
    val isLoading: LiveData<Boolean>
    val userLogin: LiveData<LoginResponseDto>

    suspend fun login(loginRequestAccount: LoginRequest)
    suspend fun register(registDataUser: SignUpRequest)
    suspend fun upload(
        photo: MultipartBody.Part,
        des: RequestBody,
        lat: Double?,
        lng: Double?,
        token: String
    )
    suspend fun getStories(token: String)

    @ExperimentalPagingApi
    fun getPagingStories(token: String): LiveData<PagingData<StoryEntity>>
}