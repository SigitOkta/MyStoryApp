package com.dwarf.mystoryapp.ui.main

import androidx.lifecycle.*
import com.dwarf.mystoryapp.data.local.datastore.UserPreferences
import com.dwarf.mystoryapp.data.repositorty.StoryRepository
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

