package com.dwarf.mystoryapp.data.repositorty

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dwarf.mystoryapp.data.FakeApiService
import com.dwarf.mystoryapp.data.Result
import com.dwarf.mystoryapp.data.remote.retrofit.ApiService
import com.dwarf.mystoryapp.utils.DataDummy
import com.dwarf.mystoryapp.utils.MainDispatcherRule
import com.dwarf.mystoryapp.utils.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UserRepositoryTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var apiService: ApiService
    private lateinit var userRepository: UserRepository

    private val dummyName = "dummyName"
    private val dummyEmail = "tes@gmail.com"
    private val dummyPassword = "123456"


    @Before
    fun setUp() {
        apiService = FakeApiService()
        userRepository = UserRepository(apiService)
    }

    @Test
    fun `when login should not null and return success`() = runTest{
        val expectedResponse = DataDummy.generateDummyLoginResponse()
        val actualResponse = userRepository.login(dummyEmail,dummyPassword)
        actualResponse.observeForTesting {
            assertNotNull(actualResponse)
            assertEquals(
                expectedResponse,
                (actualResponse.value as? Result.Success)?.data
            )
        }
    }

    @Test
    fun `when signup should not null and return success`() = runTest {
        val expectedResponse = DataDummy.generateDummySignupResponse()
        val actualResponse = userRepository.signup(dummyName,dummyEmail,dummyPassword)
        actualResponse.observeForTesting {
            assertNotNull(actualResponse)
            assertEquals(
                expectedResponse,
                (actualResponse.value as? Result.Success)?.data
            )
        }
    }
}