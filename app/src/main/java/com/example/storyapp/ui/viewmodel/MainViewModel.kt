package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.contract.RemoteDataRepository
import com.example.storyapp.data.local.room.entity.StoryEntity
import com.example.storyapp.data.remote.dto.request.LoginRequest
import com.example.storyapp.data.remote.dto.request.SignUpRequest
import com.example.storyapp.data.remote.dto.response.LoginResponseDto
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val remoteDataRepositoryImpl: RemoteDataRepository) : ViewModel() {

    val stories: LiveData<List<StoryEntity>> = remoteDataRepositoryImpl.stories

    val message: LiveData<String> = remoteDataRepositoryImpl.message

    val isLoading: LiveData<Boolean> = remoteDataRepositoryImpl.isLoading

    val userlogin: LiveData<LoginResponseDto> = remoteDataRepositoryImpl.userLogin

    private val _location = MutableLiveData<LatLng?>()
    val location: LiveData<LatLng?> get() = _location

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            remoteDataRepositoryImpl.login(loginRequest)
        }
    }

    fun signup(signupRequest: SignUpRequest) {
        viewModelScope.launch {
            remoteDataRepositoryImpl.register(signupRequest)
        }
    }

    fun upload(
        photo: MultipartBody.Part,
        des: RequestBody,
        lat: Double?,
        lng: Double?,
        token: String
    ) {
        viewModelScope.launch {
            remoteDataRepositoryImpl.upload(photo, des, lat, lng, token)
        }
    }

    @ExperimentalPagingApi
    fun getPagingStories(token: String): LiveData<PagingData<StoryEntity>> {
        return remoteDataRepositoryImpl.getPagingStories(token).cachedIn(viewModelScope)
    }

    fun getStories(token: String) {
        viewModelScope.launch {
            remoteDataRepositoryImpl.getStories(token)
        }
    }
}