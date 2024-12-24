package com.example.storyapp.data.remote.dto.response

data class LoginResultDto(
    var userId: String,
    var name: String,
    var token: String
)