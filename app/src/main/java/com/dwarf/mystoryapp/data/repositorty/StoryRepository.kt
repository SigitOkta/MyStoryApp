package com.dwarf.mystoryapp.data.repositorty

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dwarf.mystoryapp.data.Result
import com.dwarf.mystoryapp.data.remote.response.AddStoryResponse
import com.dwarf.mystoryapp.data.remote.response.ListStoryItem
import com.dwarf.mystoryapp.data.remote.response.StoriesResponse
import com.dwarf.mystoryapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
            emit(Result.Error(e.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun addNewStory(
        token: String,
        description: RequestBody,
        imageMultipart: MultipartBody.Part
    ): LiveData<Result<AddStoryResponse>> = liveData(Dispatchers.IO){
        emit(Result.Loading)
        try {
            val response = apiService.addStory("Bearer $token",description,imageMultipart)
            if (!response.error) {
                emit(Result.Success(response))
                Log.d("addNewStory1",response.toString())
            } else {
                Log.d("addNewStory2",response.message)
                emit(Result.Error(response.message))
            }
        }catch (e: HttpException) {
            val responseBody =
                Gson().fromJson(e.response()?.errorBody()?.string(), StoriesResponse::class.java)
            Log.d("addNewStory3",responseBody.message)
            emit(Result.Error(responseBody.message))
        } catch (e: IOException) {
            Log.d("addNewStory4",e.toString())
            emit(Result.Error(e.message.toString()))
        } catch (e: Exception) {
            Log.d("addNewStory5",e.toString())
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