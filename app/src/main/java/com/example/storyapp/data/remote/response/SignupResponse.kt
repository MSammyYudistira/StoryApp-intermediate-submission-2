package com.example.storyapp.data.remote.response

import com.example.storyapp.data.local.entity.ListStoryDetail
import com.google.gson.annotations.SerializedName

data class SignupData(
    var name: String,
    var email: String,
    var password: String
)

data class LoginData(
    var email: String,
    var password: String
)

data class DetailResponse(
    var error: Boolean,
    var message: String
)

data class LoginResponse(
    var error: Boolean,
    var message: String,
    var loginResult: LoginResult
)

data class LoginResult(
    var userId: String,
    var name: String,
    var token: String
)

data class LocationStoryResponse(
    @field:SerializedName("error")
    var error: String,

    @field:SerializedName("message")
    var message: String,

    @field:SerializedName("listStory")
    var listStory: List<ListStoryDetail>
)

data class StoryPagingResponse(
    @field:SerializedName("error")
    var error: String,

    @field:SerializedName("message")
    var message: String,

    @field:SerializedName("listStory")
    var listStory: List<ListStoryDetail>
)
