package com.dwarf.mystoryapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dwarf.mystoryapp.data.local.datastore.UserPreferences
import com.dwarf.mystoryapp.data.repositorty.StoryRepository
import com.dwarf.mystoryapp.di.Injection
import com.dwarf.mystoryapp.ui.addstory.AddStoryViewModel
import com.dwarf.mystoryapp.ui.main.MainViewModel

class StoryViewModelFactory(
    private val userPreferences: UserPreferences,
    private val storyRepository: StoryRepository
    ) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userPreferences,storyRepository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(userPreferences,storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: StoryViewModelFactory? = null

        fun getInstance(
            userPreferences: UserPreferences,
            context: Context
        ): StoryViewModelFactory = instance ?: synchronized(this) {
            instance ?: StoryViewModelFactory(
                userPreferences, Injection.provideStoryRepository(context)
            )
        }.also { instance = it }
    }

}