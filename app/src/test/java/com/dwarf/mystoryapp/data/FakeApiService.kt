package com.dwarf.mystoryapp.data

import com.dwarf.mystoryapp.data.remote.response.AddStoryResponse
import com.dwarf.mystoryapp.data.remote.response.LoginResponse
import com.dwarf.mystoryapp.data.remote.response.SignupResponse
import com.dwarf.mystoryapp.data.remote.response.StoriesResponse
import com.dwarf.mystoryapp.data.remote.retrofit.ApiService
import com.dwarf.mystoryapp.utils.DataDummy
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService : ApiService {

    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummySignupResponse = DataDummy.generateDummySignupResponse()
    private val dummyAddStoryResponse = DataDummy.generateDummyAddStoryResponse()
    private val dummyStoriesResponse = DataDummy.generateDummyStoryResponse()

    override suspend fun loginUser(email: String, password: String): LoginResponse = dummyLoginResponse

    override suspend fun signupUser(name: String, email: String, password: String): SignupResponse {
        return dummySignupResponse
    }

    override suspend fun addStory(
        token: String,
        description: RequestBody,
        file: MultipartBody.Part,
        lat: RequestBody,
        lon: RequestBody
    ): AddStoryResponse {
        return dummyAddStoryResponse
    }

    override suspend fun getAllStories(
        token: String,
        page: Int?,
        size: Int?,
        location: Int?
    ): StoriesResponse {
        return dummyStoriesResponse
    }
}