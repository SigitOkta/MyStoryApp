package com.dwarf.mystoryapp.di

import android.content.Context
import com.dwarf.mystoryapp.data.local.room.StoryDatabase
import com.dwarf.mystoryapp.data.remote.retrofit.ApiConfig
import com.dwarf.mystoryapp.data.repositorty.StoryRepository
import com.dwarf.mystoryapp.data.repositorty.UserRepository

object Injection {
    fun provideUserRepository(): UserRepository {
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService)
    }
    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getInstance(context)
        return StoryRepository.getInstance(apiService,database)
    }

}