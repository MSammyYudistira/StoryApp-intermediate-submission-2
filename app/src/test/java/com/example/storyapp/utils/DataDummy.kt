package com.example.storyapp.utils

import com.example.storyapp.data.local.entity.ListStoryDetail
import com.example.storyapp.data.remote.response.LoginData
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.LoginResult
import com.example.storyapp.data.remote.response.SignupData

object DataDummy {

    fun generateDummyNewsEntity(): List<ListStoryDetail> {
        val newsList = ArrayList<ListStoryDetail>()
        for (i in 0..5) {
            val stories = ListStoryDetail(
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

    fun generateDummyNewStories(): List<ListStoryDetail> {
        val newsList = ArrayList<ListStoryDetail>()
        for (i in 0..5) {
            val stories = ListStoryDetail(
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


    fun generateDummyRequestLogin(): LoginData {
        return LoginData("sammy123@gmail.com", "asd111111")
    }

    fun generateDummyResponseLogin(): LoginResponse {
        val newLoginResult = LoginResult("123123", "sammy", "INI_TOKEN")
        return LoginResponse(false, "Login successfully", newLoginResult)
    }

    fun generateDummyRequestRegister(): SignupData {
        return SignupData("sammy", "yudis123@gmail.com", "qwerty123")
    }

}