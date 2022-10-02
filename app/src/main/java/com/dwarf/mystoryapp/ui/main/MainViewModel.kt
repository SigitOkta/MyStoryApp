package com.dwarf.mystoryapp.ui.main

import androidx.lifecycle.*
import com.dwarf.mystoryapp.data.local.datastore.UserPreferences
import com.dwarf.mystoryapp.data.repositorty.StoryRepository
import com.dwarf.mystoryapp.di.Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val userPreferences: UserPreferences,
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getToken(): LiveData<String> {
        return userPreferences.getToken().asLiveData()
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.deleteToken()
        }
    }

    fun getAllStories(token: String) = storyRepository.getAllStories(token)
}

class MainViewModelFactory private constructor(
    private val userPreferences: UserPreferences,
    private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(userPreferences, storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: MainViewModelFactory? = null

        fun getInstance(
            userPreferences: UserPreferences,
        ): MainViewModelFactory = instance ?: synchronized(this) {
            instance ?: MainViewModelFactory(
                userPreferences, Injection.provideStoryRepository()
            )
        }.also { instance = it }
    }
}