package com.example.storyapp.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.example.storyapp.data.contract.RemoteDataRepository
import com.example.storyapp.data.local.room.entity.StoryEntity
import com.example.storyapp.data.remote.dto.request.LoginRequest
import com.example.storyapp.data.remote.dto.request.SignUpRequest
import com.example.storyapp.data.remote.dto.response.LoginResponseDto
import com.example.storyapp.data.remote.mediator.StoryRemoteMediator
import com.example.storyapp.data.remote.ApiConfig
import com.example.storyapp.data.remote.ApiService
import com.example.storyapp.utils.wrapEspressoIdlingResource
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RemoteDataRepositoryImpl(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
): RemoteDataRepository {
    private var _stories = MutableLiveData<List<StoryEntity>>()
    override var stories: LiveData<List<StoryEntity>> = _stories

    private val _message = MutableLiveData<String>()
    override val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    override val isLoading: LiveData<Boolean> = _isLoading

    private val _userLogin = MutableLiveData<LoginResponseDto>()
    override val userLogin: LiveData<LoginResponseDto> = _userLogin

    override suspend fun login(loginRequestAccount: LoginRequest) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            val api = ApiConfig.getApiService().login(loginRequestAccount)
            _isLoading.value = false
            if (api.isSuccessful) {
                _userLogin.value = api.body()!!
                _message.value = "Hello ${_userLogin.value!!.loginResultDto.name}!"
            } else {
                when (api.code()) {
                    401 -> _message.value =
                        "Your Email or Password Incorrect, Please Try Again"

                    408 -> _message.value =
                        "Your Connection Internet Running Slow, Please Try Again"

                    else -> _message.value = "Error Message: " + api.message()
                }
            }
        }
    }

    override suspend fun register(registDataUser: SignUpRequest) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            val response = ApiConfig.getApiService().register(registDataUser)
            _isLoading.value = false
            if (response.isSuccessful) {
                _message.value = "Account Created Successfully"
            } else {
                when (response.code()) {
                    400 -> _message.value =
                        "Email Already Taken, Please Login With Different Email"

                    408 -> _message.value =
                        "Your Connection Internet Running Slow, Please Try Again"

                    else -> _message.value = "Message error: " + response.message()
                }
            }

        }
    }

    override suspend fun upload(
        photo: MultipartBody.Part,
        des: RequestBody,
        lat: Double?,
        lng: Double?,
        token: String
    ) {
        _isLoading.value = true
        val response = ApiConfig.getApiService().upload(
            photo, des, lat?.toFloat(), lng?.toFloat(), "Bearer $token"
        )
        _isLoading.value = false
        if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null && !responseBody.error) {
                _message.value = responseBody.message
            }
        } else {
            _message.value = response.message()
        }
    }

    override suspend fun getStories(token: String) {
        _isLoading.value = true
        val response = ApiConfig.getApiService().getLocationStory(32, 1, "Bearer $token")
        _isLoading.value = false
        if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                _stories.value = responseBody.listStory
            }
            _message.value = responseBody?.message.toString()

        } else {
            _message.value = response.message()
        }
    }

    @ExperimentalPagingApi
    override fun getPagingStories(token: String): LiveData<PagingData<StoryEntity>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.getListStoryDetailDao().getAllStory()
            }
        )
        return pager.liveData
    }
}