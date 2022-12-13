package com.dwarf.mystoryapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dwarf.mystoryapp.data.Result
import com.dwarf.mystoryapp.data.local.datastore.UserPreferences
import com.dwarf.mystoryapp.data.remote.response.LoginResponse
import com.dwarf.mystoryapp.data.repositorty.UserRepository
import com.dwarf.mystoryapp.utils.DataDummy
import com.dwarf.mystoryapp.utils.MainDispatcherRule
import com.dwarf.mystoryapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest{

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userPreferences: UserPreferences
    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var loginViewModel: LoginViewModel
    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummyUserEntity = DataDummy.generateDummyUserEntity()
    private val dummyEmail = "tes@gmail.com"
    private val dummyPassword = "123456"
    private val dummyToken = "token1234567"


    @Before
    fun setUp(){
        loginViewModel = LoginViewModel(userRepository,userPreferences)
    }

    @Test
    fun `when do login should not null and return success`(){
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.Success(dummyLoginResponse)
        `when`(userRepository.login(dummyEmail,dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = loginViewModel.loginUser(dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(userRepository).login(dummyEmail, dummyPassword)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
        assertEquals(dummyLoginResponse.loginResult,
            (actualResponse as Result.Success).data.loginResult
        )
    }

    @Test
    fun `when do login return error`() {
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.Error("Error")
        `when`(userRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = loginViewModel.loginUser(dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(userRepository).login(dummyEmail, dummyPassword)
        assertTrue(actualResponse is Result.Error)
    }

    @Test
    fun `when save user entity then call saveUser`() = runTest{
        loginViewModel.saveUser(dummyUserEntity)
        Mockito.verify(userPreferences).saveUser(dummyUserEntity)
    }

    @Test
    fun `when token not null then call login`() = runTest{
        val actualResponse = loginViewModel.login(dummyToken)
        assertNotNull(actualResponse)
        Mockito.verify(userPreferences).login(dummyToken)
    }
}