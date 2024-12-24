package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.ApiConfig
import com.example.storyapp.data.contract.RemoteDataRepository
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.repository.RemoteDataRepositoryImpl

object Injection {
    fun provideRepository(context: Context): RemoteDataRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return RemoteDataRepositoryImpl(database, apiService)
    }
}