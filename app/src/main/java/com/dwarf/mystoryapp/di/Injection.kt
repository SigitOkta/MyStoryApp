package com.dwarf.mystoryapp.di

import com.dwarf.mystoryapp.data.remote.retrofit.ApiConfig
import com.dwarf.mystoryapp.data.remote.retrofit.ApiService
import com.dwarf.mystoryapp.data.repositorty.StoryRepository
import com.dwarf.mystoryapp.data.repositorty.UserRepository

object Injection {
    fun provideUserRepository(): UserRepository {
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService)
    }
    fun provideStoryRepository(): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService)
    }

}