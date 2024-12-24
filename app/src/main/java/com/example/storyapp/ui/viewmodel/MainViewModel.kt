package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.local.entity.ListStoryDetail
import com.example.storyapp.data.remote.repository.MainRepository
import com.example.storyapp.data.remote.response.LoginData
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.SignupData
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    val stories: LiveData<List<ListStoryDetail>> = mainRepository.stories

    val message: LiveData<String> = mainRepository.message

    val isLoading: LiveData<Boolean> = mainRepository.isLoading

    val userlogin: LiveData<LoginResponse> = mainRepository.userlogin

    fun login(loginData: LoginData) {
        viewModelScope.launch {
        mainRepository.getLoginResponse(loginData)
    }
    }

    fun signup(signupData: SignupData) {
        viewModelScope.launch {
        mainRepository.getResponseRegister(signupData)
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
        mainRepository.upload(photo, des, lat, lng, token)
    }
    }

    @ExperimentalPagingApi
    fun getPagingStories(token: String): LiveData<PagingData<ListStoryDetail>> {
        return mainRepository.getPagingStories(token).cachedIn(viewModelScope)
    }

    fun getStories(token: String) {
        mainRepository.getStories(token)
    }
}