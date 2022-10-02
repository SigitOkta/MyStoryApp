package com.dwarf.mystoryapp.data.repositorty

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dwarf.mystoryapp.data.Result
import com.dwarf.mystoryapp.data.remote.response.LoginResponse
import com.dwarf.mystoryapp.data.remote.response.SignupResponse
import com.dwarf.mystoryapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import okio.IOException
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: ApiService
) {
    fun signup(name: String, email: String, password: String): LiveData<Result<SignupResponse>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val response = apiService.signupUser(name, email, password)
                if (!response.error) {
                    emit(Result.Success(response))
                } else {
                    emit(Result.Error(response.message))

                }
            } catch (e: HttpException) {
                val responseBody =
                    Gson().fromJson(e.response()?.errorBody()?.string(), SignupResponse::class.java)
                emit(Result.Error(responseBody.message))
            } catch (e: IOException) {
                emit(Result.Error(e.message.toString()))
            }
        }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val response = apiService.loginUser(email, password)
                if (!response.error) {
                    emit(Result.Success(response))
                } else {
                    emit(Result.Error(response.message))
                    Log.d("login", "error0: ${response.message} ")
                }
            } catch (e: HttpException) {
                val responseBody =
                    Gson().fromJson(e.response()?.errorBody()?.string(), SignupResponse::class.java)
                emit(Result.Error(responseBody.message))
            } catch (e: IOException) {
                emit(Result.Error(e.message.toString()))
            }
        }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService)
            }.also { instance = it }
    }
}