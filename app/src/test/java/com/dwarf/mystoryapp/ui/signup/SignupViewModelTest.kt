package com.dwarf.mystoryapp.ui.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dwarf.mystoryapp.data.Result
import com.dwarf.mystoryapp.data.remote.response.SignupResponse
import com.dwarf.mystoryapp.data.repositorty.UserRepository
import com.dwarf.mystoryapp.utils.DataDummy
import com.dwarf.mystoryapp.utils.getOrAwaitValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SignupViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var signupViewModel: SignupViewModel
    private var dummySignUpResponse = DataDummy.generateDummySignupResponse()
    private val dummyName = "dummyName"
    private val dummyEmail = "tes@gmail.com"
    private val dummyPassword = "123456"


    @Before
    fun setUp() {
        signupViewModel = SignupViewModel(userRepository)
    }

    @Test
    fun `when do signup should not null and return success`() {
        val expectedResponse = MutableLiveData<Result<SignupResponse>>()
        expectedResponse.value = Result.Success(dummySignUpResponse)
        `when`(userRepository.signup(dummyName, dummyEmail, dummyPassword)).thenReturn(
            expectedResponse
        )
        val actualResponse =
            signupViewModel.signUpUser(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(userRepository).signup(dummyName, dummyEmail, dummyPassword)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
        assertEquals(
            dummySignUpResponse,
            (actualResponse as Result.Success).data
        )
    }

    @Test
    fun `when do signup and return error`() {
        val expectedResponse = MutableLiveData<Result<SignupResponse>>()
        expectedResponse.value = Result.Error("Error")
        `when`(userRepository.signup(dummyName, dummyEmail, dummyPassword))
            .thenReturn(expectedResponse)
        val actualResponse =
            signupViewModel.signUpUser(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(userRepository).signup(dummyName, dummyEmail, dummyPassword)
        assertTrue(actualResponse is Result.Error)
    }
}