package com.example.storyapp.data.remote.dto.response

import com.example.storyapp.data.local.room.entity.StoryEntity
import com.google.gson.annotations.SerializedName

data class LocationStoryResponseDto(
    @field:SerializedName("error")
    var error: String,

    @field:SerializedName("message")
    var message: String,

    @field:SerializedName("listStory")
    var listStory: List<StoryEntity>
)