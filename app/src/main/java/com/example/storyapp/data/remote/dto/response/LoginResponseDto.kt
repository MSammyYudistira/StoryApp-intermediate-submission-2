package com.example.storyapp.data.remote.dto.response

data class LoginResponseDto(
    var error: Boolean,
    var message: String,
    var loginResultDto: LoginResultDto
)
