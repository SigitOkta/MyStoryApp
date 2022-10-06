package com.dwarf.mystoryapp.ui.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dwarf.mystoryapp.data.local.datastore.UserPreferences
import com.dwarf.mystoryapp.data.repositorty.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(
    private val userPreferences: UserPreferences,
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getToken(): LiveData<String> {
        return userPreferences.getToken().asLiveData()
    }

    fun addNewStory(token: String, description: RequestBody, imageMultipart: MultipartBody.Part) =
        storyRepository.addNewStory(token, description, imageMultipart)
}