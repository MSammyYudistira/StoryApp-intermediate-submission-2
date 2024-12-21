package com.example.storyapp.data.di

import android.content.Context
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.repository.MainRepository
import com.example.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): MainRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return MainRepository(database, apiService)
    }
}