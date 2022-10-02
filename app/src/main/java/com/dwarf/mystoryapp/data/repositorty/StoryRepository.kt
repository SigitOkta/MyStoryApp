package com.dwarf.mystoryapp.data.repositorty

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dwarf.mystoryapp.data.Result
import com.dwarf.mystoryapp.data.remote.response.ListStoryItem
import com.dwarf.mystoryapp.data.remote.response.StoriesResponse
import com.dwarf.mystoryapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import okio.IOException
import retrofit2.HttpException

class StoryRepository private constructor(
    private val apiService: ApiService
) {
    fun getAllStories(token: String): LiveData<Result<List<ListStoryItem>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.getAllStories("Bearer $token")
            if (!response.error) {
                emit(Result.Success(response.listStory))
            } else {
                emit(Result.Error(response.message))
            }
        } catch (e: HttpException) {
            val responseBody =
                Gson().fromJson(e.response()?.errorBody()?.string(), StoriesResponse::class.java)
            emit(Result.Error(responseBody.message))
        } catch (e: IOException) {
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }

}