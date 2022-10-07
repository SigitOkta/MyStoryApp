package com.dwarf.mystoryapp.data.remote.retrofit

import com.dwarf.mystoryapp.data.remote.response.AddStoryResponse
import com.dwarf.mystoryapp.data.remote.response.LoginResponse
import com.dwarf.mystoryapp.data.remote.response.SignupResponse
import com.dwarf.mystoryapp.data.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun signupUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SignupResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): AddStoryResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String
    ): StoriesResponse
}