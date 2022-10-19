package com.dwarf.mystoryapp.ui.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dwarf.mystoryapp.data.local.datastore.UserPreferences
import com.dwarf.mystoryapp.data.local.entity.StoryEntity
import com.dwarf.mystoryapp.data.local.entity.UserEntity
import com.dwarf.mystoryapp.data.repositorty.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val userPreferences: UserPreferences,
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getUser(): LiveData<UserEntity> {
        return userPreferences.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.logout()
        }
    }

    fun getAllStories(token : String): LiveData<PagingData<StoryEntity>> =
        storyRepository.getAllStories(token).cachedIn(viewModelScope)


}

