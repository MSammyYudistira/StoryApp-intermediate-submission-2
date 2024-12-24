package com.example.storyapp.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.example.storyapp.data.local.entity.ListStoryDetail
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.response.DetailResponse
import com.example.storyapp.data.remote.response.LocationStoryResponse
import com.example.storyapp.data.remote.response.LoginData
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.SignupData
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.wrapEspressoIdlingResource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    private var _stories = MutableLiveData<List<ListStoryDetail>>()
    var stories: LiveData<List<ListStoryDetail>> = _stories

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userLogin = MutableLiveData<LoginResponse>()
    var userlogin: LiveData<LoginResponse> = _userLogin

    suspend fun getLoginResponse(loginDataAccount: LoginData) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            val api = ApiConfig.getApiService().userLogin(loginDataAccount)
//            api.enqueue(object : Callback<LoginResponse> {
//                override fun onResponse(
//                    call: Call<LoginResponse>,
//                    response: Response<LoginResponse>
//                ) {
//                    _isLoading.value = false
//                    val responseBody = response.body()
//
                    if (api.isSuccessful) {
                        _userLogin.value = api.body()!!
                        _message.value = "Hello ${_userLogin.value!!.loginResult.name}!"
                    } else {
                        when (api.code()) {
                            401 -> _message.value =
                                "Your Email or Password Incorrect, Please Try Again"
                            408 -> _message.value =
                                "Your Connection Internet Running Slow, Please Try Again"
                            else -> _message.value = "Error Message: " + api.message()
                        }
                    }
//                }

//                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                    _isLoading.value = false
//                    _message.value = "Pesan error: " + t.message.toString()
//                }

            }
        }
    }

    suspend fun getResponseRegister(registDataUser: SignupData) {
        wrapEspressoIdlingResource {
            _isLoading.value = true
            val api = ApiConfig.getApiService().userSignup(registDataUser)
//            api.enqueue(object : Callback<DetailResponse> {
//                override fun onResponse(
//                    call: Call<DetailResponse>,
//                    response: Response<DetailResponse>
//                ) {
                    _isLoading.value = false
                    if (api.isSuccessful) {
                        _message.value = "Account Created Successfully"
                    } else {
                        when (api.code()) {
                            400 -> _message.value =
                                "Email Already Taken, Please Login With Different Email"
                            408 -> _message.value =
                                "Your Connection Internet Running Slow, Please Try Again"
                            else -> _message.value = "Message error: " + api.message()
                        }
                    }
//                }

//                override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
//                    _isLoading.value = false
//                    _message.value = "Message error: " + t.message.toString()
//                }


        }
    }

    suspend fun upload(
        photo: MultipartBody.Part,
        des: RequestBody,
        lat: Double?,
        lng: Double?,
        token: String
    ) {
        _isLoading.value = true
        val service = ApiConfig.getApiService().uploadImage(
            photo, des, lat?.toFloat(), lng?.toFloat(), "Bearer $token"
        )
        service.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
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

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _isLoading.value = false
                _message.value = t.message
            }
        })
    }

    suspend fun getStories(token: String) {
        _isLoading.value = true
        val api = ApiConfig.getApiService().getLocationStory(32, 1, "Bearer $token")
        api.enqueue(object : Callback<LocationStoryResponse> {
            override fun onResponse(
                call: Call<LocationStoryResponse>,
                response: Response<LocationStoryResponse>
            ) {
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

            override fun onFailure(call: Call<LocationStoryResponse>, t: Throwable) {
                _isLoading.value = false
                _message.value = t.message.toString()
            }
        })
    }

    @ExperimentalPagingApi
    fun getPagingStories(token: String): LiveData<PagingData<ListStoryDetail>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.getListStoryDetailDao().getAllStories()
            }
        )
        return pager.liveData
    }