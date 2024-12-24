package com.example.storyapp.utils

import com.example.storyapp.data.local.room.entity.StoryEntity
import com.example.storyapp.data.remote.dto.request.LoginRequest
import com.example.storyapp.data.remote.dto.request.SignUpRequest
import com.example.storyapp.data.remote.dto.response.LoginResponseDto
import com.example.storyapp.data.remote.dto.response.LoginResultDto

object DataDummy {

    fun generateDummyNewsEntity(): List<StoryEntity> {
        val newsList = ArrayList<StoryEntity>()
        for (i in 0..5) {
            val stories = StoryEntity(
                "Title $i",
                "this is name",
                "This is description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                null,
                null,
            )
            newsList.add(stories)
        }
        return newsList
    }

    fun generateDummyNewStories(): List<StoryEntity> {
        val newsList = ArrayList<StoryEntity>()
        for (i in 0..5) {
            val stories = StoryEntity(
                "Title $i",
                "this is name",
                "This is description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                null,
                null,
            )
            newsList.add(stories)
        }
        return newsList
    }


    fun generateDummyRequestLogin(): LoginRequest {
        return LoginRequest("sammy123@gmail.com", "asd111111")
    }

    fun generateDummyResponseLogin(): LoginResponseDto {
        val newLoginResult = LoginResultDto("123123", "sammy", "INI_TOKEN")
        return LoginResponseDto(false, "Login successfully", newLoginResult)
    }

    fun generateDummyRequestRegister(): SignUpRequest {
        return SignUpRequest("sammy", "yudis123@gmail.com", "qwerty123")
    }

}