package com.dwarf.mystoryapp.data.repositorty

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.dwarf.mystoryapp.data.Result
import com.dwarf.mystoryapp.data.StoryRemoteMediator
import com.dwarf.mystoryapp.data.local.entity.StoryEntity
import com.dwarf.mystoryapp.data.local.room.StoryDatabase
import com.dwarf.mystoryapp.data.remote.response.AddStoryResponse
import com.dwarf.mystoryapp.data.remote.response.StoriesResponse
import com.dwarf.mystoryapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.IOException
import retrofit2.HttpException

class StoryRepository constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
) {

    fun getAllStories(token: String): LiveData<PagingData<StoryEntity>> =
        @OptIn(ExperimentalPagingApi::class)
        Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData

    fun getAllStoriesWithLocation(token: String): LiveData<Result<List<StoryEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAllStories("Bearer $token", location = 1)
            val stories = response.listStory
            val storyList = stories.map { story ->
                StoryEntity(
                    story.id,
                    story.photoUrl,
                    story.createdAt,
                    story.name,
                    story.description,
                    story.lon,
                    story.lat
                )
            }
            emit(Result.Success(storyList))
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
        imageMultipart: MultipartBody.Part,
        lat: RequestBody?,
        lon: RequestBody?,
    ): LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.addStory("Bearer $token",description,imageMultipart,lat,lon)
            if (!response.error) {
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message))
            }
        }catch (e: HttpException) {
            val responseBody =
                Gson().fromJson(e.response()?.errorBody()?.string(), StoriesResponse::class.java)
            emit(Result.Error(responseBody.message))
        } catch (e: IOException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            storyDatabase: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService,storyDatabase)
            }.also { instance = it }
    }

}