package com.dwarf.mystoryapp.data.remote.retrofit

import com.dwarf.mystoryapp.data.remote.response.AddStoryResponse
import com.dwarf.mystoryapp.data.remote.response.LoginResponse
import com.dwarf.mystoryapp.data.remote.response.SignupResponse
import com.dwarf.mystoryapp.data.remote.response.StoriesResponse
import okhttp3.MultipartBody
import retrofit2.Call
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
    fun addStory(
        @Header("Authorization") token: String,
        @Part("description") des: String,
        @Part file: MultipartBody.Part
    ): AddStoryResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String
    ): StoriesResponse
}